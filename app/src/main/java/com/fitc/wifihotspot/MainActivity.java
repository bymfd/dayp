package com.fitc.wifihotspot;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends PermissionsActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SHOW_ICON = "show_icon" ;

    public boolean torchmode=false;
    public boolean sesmode=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //start hotspott



        Intent intent = new Intent(getString(R.string.intent_action_turnon));
        sendImplicitBroadcast(this,intent);



    }




    @Override
    void onPermissionsOkay() {

    }


    public void onClickTurnOnAction(View v){
        MediaPlayer ring = MediaPlayer.create(MainActivity.this, R.raw.sos);

        ring.setVolume(80, 80);
        if (!sesmode) {
            ring.setLooping(true); // Set looping
            ring.start();
            sesmode=true;
        }else{
            ring.setLooping(false); // Set looping
            ring.stop();ring.setLooping(false); // Set looping
            sesmode=false;

        }


    }
    public void onClickTurnOffAction(View v){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("tel:" + "112"));
        startActivity(intent);
    }
    public void onClickTurnOnData(View v){
       if (torchmode==false){
      CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String cameraId = null;
        try {
            cameraId = camManager.getCameraIdList()[0];
            camManager.setTorchMode(cameraId, true);   //Turn ON
            torchmode=true;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

       torchmode=true;
       }
       else{

           CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
           String cameraId = null;
           try {
               cameraId = camManager.getCameraIdList()[0];
               camManager.setTorchMode(cameraId, false);   //Turn ON
           } catch (CameraAccessException e) {
               e.printStackTrace();
           }
            torchmode=false;
       }

    }

    private static void sendImplicitBroadcast(Context ctxt, Intent i) {
        PackageManager pm=ctxt.getPackageManager();
        List<ResolveInfo> matches=pm.queryBroadcastReceivers(i, 0);

        for (ResolveInfo resolveInfo : matches) {
            Intent explicit=new Intent(i);
            ComponentName cn=
                    new ComponentName(resolveInfo.activityInfo.applicationInfo.packageName,
                            resolveInfo.activityInfo.name);

            explicit.setComponent(cn);
            ctxt.sendBroadcast(explicit);
        }
    }


}
