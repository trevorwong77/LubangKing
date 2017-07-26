package com.innotech.lubang;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.innotech.lib.ShakeDetectActivity;
import com.innotech.lib.ShakeDetectActivityListener;

/**
 * Created by itman on 14/3/2015.
 */
public class LubangService  extends Service {


    ShakeDetectActivity mShakeDetectActivity;
    ShakeDetectActivityListener mListener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mShakeDetectActivity = new ShakeDetectActivity(this);

        mListener = new ShakeDetectActivityListener() {
            @Override
            public void shakeDetected() {
                LubangService.this.triggerShakeDetected();

            }
        };
        mShakeDetectActivity.addListener(mListener);

        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return  START_STICKY;
    }

    int mId = 0;
    public void triggerShakeDetected() {

        ////TODO: Change the icon according to the strenght of bump

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Lubang")
                        .setContentText("A bump is detected, are you ok?");

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification noti = mBuilder.build();
        noti.flags =  Notification.FLAG_AUTO_CANCEL;


        mNotificationManager.notify(mId, noti);


        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //Set the pattern, like vibrate for 300 milliseconds and then stop for 200 ms, then
        //vibrate for 300 milliseconds and then stop for 500 ms and repeat the same style. You can change the pattern and
        // test the result for better clarity.
        long pattern[]={0,200,300,500};
        //start vibration with repeated count, use -1 if you don't want to repeat the vibration
        vibrator.vibrate(pattern, -1);
    }

    @Override
    public void onDestroy()
    {
        mShakeDetectActivity.removeListener(mListener);

        super.onDestroy();

        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();

    }


    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}