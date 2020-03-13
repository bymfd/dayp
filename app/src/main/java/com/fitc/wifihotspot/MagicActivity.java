package com.fitc.wifihotspot;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.AndroidException;
import android.util.Log;
import android.widget.Toast;

public class MagicActivity extends PermissionsActivity {


    public static void useMagicActivityToTurnOn(Context c){

        Uri uri = new Uri.Builder().scheme(c.getString(R.string.intent_data_scheme)).authority(c.getString(R.string.intent_data_host_turnon)).build();
        //Toast.makeText(c,"Turn on. Uri: "+uri.toString(),Toast.LENGTH_LONG).show();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(uri);
        c.startActivity(i);
    }

    public static void useMagicActivityToTurnOff(Context c){
        Uri uri = new Uri.Builder().scheme(c.getString(R.string.intent_data_scheme)).authority(c.getString(R.string.intent_data_host_turnoff)).build();
     //   Toast.makeText(c,"Turn off. Uri: "+uri.toString(),Toast.LENGTH_LONG).show();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(uri);
        c.startActivity(i);
    }

    private static final String TAG = MagicActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // Log.e(TAG, "onCreate");

    }

    @Override
    void onPermissionsOkay() {


        issueNotification();


        carryOnWithHotSpotting();
    }


    /**
     * The whole purpose of this activity - to start {@link HotSpotIntentService}
     * This may be called straright away in {@code onCreate} or after permissions granted.
     */
    private void carryOnWithHotSpotting() {
        Intent intent = getIntent();
        HotSpotIntentService.start(this, intent);

        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void makeNotificationChannel(String id, String name, int importance)
    {
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setShowBadge(false); // set false to disable badges, Oreo exclusive

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }
    void issueNotification()
    {

        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,0);
        // make the channel. The method has been discussed before.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel("CHANNEL_1", "Sabit", NotificationManager.IMPORTANCE_HIGH);
        }
        // the check ensures that the channel will only be made
        // if the device is running Android 8+

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(this, "CHANNEL_1");
        // the second parameter is the channel id.
        // it should be the same as passed to the makeNotificationChannel() method

        notification
                .setSmallIcon(R.drawable.dayplogo) // can use any other icon
                .setContentTitle("Bir sorun mu var?")
                .setOngoing(true)
                .setAutoCancel(false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentText("Uygulamaya hızlı geçiş için tıklayın")
                .setContentIntent(pi)
                .setNumber(0); // this shows a number in the notification dots



        NotificationManager notificationManager =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.notify(1, notification.build());
        // it is better to not use 0 as notification id, so used 1.
    }

}
