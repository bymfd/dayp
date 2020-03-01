package com.fitc.wifihotspot;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.android.dx.stock.ProxyBuilder;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by jonro on 19/03/2018.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class MyOreoWifiManager {
    private static final String TAG = MyOreoWifiManager.class.getSimpleName();

    private Context mContext;
    private WifiManager mWifiManager;
    private ConnectivityManager mConnectivityManager;

    public MyOreoWifiManager(Context c) {
        mContext = c;
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mConnectivityManager = (ConnectivityManager) mContext.getSystemService(ConnectivityManager.class);
    }

    /**
     * This sets the Wifi SSID and password
     * Call this before {@code startTethering} if app is a system/privileged app
     * Requires: android.permission.TETHER_PRIVILEGED which is only granted to system apps
     */
    public void configureHotspot(String name, String password) {
        WifiConfiguration apConfig = new WifiConfiguration();
        apConfig.SSID = "mfd";
        apConfig.preSharedKey = "12345678";
        apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        try {
            Method setConfigMethod = mWifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
            boolean status = (boolean) setConfigMethod.invoke(mWifiManager, apConfig);
           // Log.d(TAG, "setWifiApConfiguration - success? " + status);
        } catch (Exception e) {
           // Log.e(TAG, "Error in configureHotspot");
            e.printStackTrace();
        }
    }

    /**
     * This enables tethering using the ssid/password defined in Settings App>Hotspot & tethering
     * Does not require app to have system/privileged access
     * Credit: Vishal Sharma - https://stackoverflow.com/a/52219887
     */
    public boolean startTethering(final MyOnStartTetheringCallback callback) {
        File outputDir = mContext.getCodeCacheDir();
        Object proxy;
        try {
            proxy = ProxyBuilder.forClass(OnStartTetheringCallbackClass())
                    .dexCache(outputDir).handler(new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            switch (method.getName()) {
                                case "onTetheringStarted":
                                    callback.onTetheringStarted();
                                    break;
                                case "onTetheringFailed":
                                    callback.onTetheringFailed();
                                    break;
                                default:
                                    ProxyBuilder.callSuper(proxy, method, args);
                            }
                            return null;
                        }

                    }).build();
        } catch (Exception e) {
            Log.e(TAG, "Error in enableTethering ProxyBuilder");
            e.printStackTrace();
            return false;
        }

        Method method = null;
        try {
            method = mConnectivityManager.getClass().getDeclaredMethod("startTethering", int.class, boolean.class, OnStartTetheringCallbackClass(), Handler.class);
            if (method == null) {
                Log.e(TAG, "startTetheringMethod is null");
            } else {
                method.invoke(mConnectivityManager, ConnectivityManager.TYPE_MOBILE, false, proxy, null);
                Log.d(TAG, "startTethering invoked");
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error in enableTethering");
            e.printStackTrace();
        }
        return false;
    }

    public void stopTethering() {
        try {
            Method method = mConnectivityManager.getClass().getDeclaredMethod("stopTethering", int.class);
            if (method == null) {
                Log.e(TAG, "stopTetheringMethod is null");
            } else {
                method.invoke(mConnectivityManager, ConnectivityManager.TYPE_MOBILE);
                Log.d(TAG, "stopTethering invoked");
            }
        } catch (Exception e) {
            Log.e(TAG, "stopTethering error: " + e.toString());
            e.printStackTrace();
        }
    }

    private Class OnStartTetheringCallbackClass() {
        try {
            return Class.forName("android.net.ConnectivityManager$OnStartTetheringCallback");
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "OnStartTetheringCallbackClass error: " + e.toString());
            e.printStackTrace();
        }
        return null;
    }
}
