package de.tum.mitfahr.gcm;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.ui.MainActivity;


public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private android.app.NotificationManager mNotificationManager;
    static final String TAG = "GcmIntentService";
    private User currentUser;

    public GcmIntentService() {
        super("GcmIntentService");
        Log.i(TAG, "Constructor");
        }

    @Override
    protected void onHandleIntent(Intent intent) {
        currentUser = TUMitfahrApplication.getApplication(getApplicationContext()).getProfileService().getUserFromPreferences();
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                showErrorMessages("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                showErrorMessages("Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                if (isCurrentUserIntended(extras)) {
                    if (extras.getString("message") != null) {

                        Log.i(TAG, "Received bundle: " + extras.toString());
                        Log.i(TAG, "Received msg: " + extras.getString("message"));


                        de.tum.mitfahr.gcm.NotificationManager.getInstance(this).updateNotifications(extras);
                    }
                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void showErrorMessages(String msg) {
        Log.i(TAG, msg);
        mNotificationManager = (android.app.NotificationManager)  this.getSystemService(Context.NOTIFICATION_SERVICE);

        // redirect to timeline
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(this.getResources().getString(R.string.messageTitle))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        // Send Notification to Extra connected devices
        sendExtraNotifications(this.getResources().getString(R.string.messageTitle), msg, this);
    }

    /*
     * Method to handle any extra notification needs to be send on other devices like Pebble
     * Any business logic about turning notification on/off to specific device should be applied in this method
     */
    public void sendExtraNotifications(String title, String body, Context context){
        PebbleNotifications.sendNotificationToPebble(title, body, context);
    }

    private boolean isCurrentUserIntended(Bundle extras) {
        String userId = extras.getString("user_id");

        try {
            int id = Integer.parseInt(userId);
            Log.i(TAG, "Received for user  " + id);
            if (id == currentUser.getId()) return true;
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }

        return false;
    }
    
}