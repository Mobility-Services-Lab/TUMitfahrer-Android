package de.tum.mitfahr.gcm;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.squareup.otto.Subscribe;

import java.util.Hashtable;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.GetRideEvent;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.response.RideResponse;
import de.tum.mitfahr.services.RidesService;
import de.tum.mitfahr.ui.MainActivity;
import de.tum.mitfahr.ui.RideDetailsActivity;
import de.tum.mitfahr.util.StringHelper;

/**
 * Created by Duygu on 03/08/2015.
 */
public class NotificationManager {

    private final static String TAG = NotificationManager.class.getSimpleName();
    private android.app.NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    private Context context;

    private RidesService ridesService;
    private String message = "";
    private String title = "";
    private boolean isDeletedRide=false;

    private static NotificationManager instance;


    public static NotificationManager getInstance(Context context) {
        if(instance==null) instance =  new NotificationManager(context);
        return instance;
    }
    private NotificationManager(Context context) {
        this.context = context;
        ridesService = TUMitfahrApplication.getApplication(context).getRidesService();
    }

    // index is used to make each PendingIntent id unique per ride
    private int index = 2;

    public PendingIntent getPendingIntentForRide(Ride ride){
        return createPendingIntent(ride, index++);
    }

    private PendingIntent createPendingIntent(Ride ride, int pendingIntentId){
        PendingIntent resultPendingIntent;
        Intent resultIntent;
        if(isDeletedRide) resultIntent = new Intent(context, MainActivity.class);
        else resultIntent = new Intent(context, RideDetailsActivity.class);
        resultIntent.putExtra(RideDetailsActivity.RIDE_INTENT_EXTRA, ride);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            // Adds the back stack
            stackBuilder.addParentStack(RideDetailsActivity.class);
            // Adds the Intent to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            // Gets a PendingIntent containing the entire back stack
            resultPendingIntent = stackBuilder.getPendingIntent(pendingIntentId, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else {
            resultPendingIntent = PendingIntent.getActivity(context, pendingIntentId, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return resultPendingIntent;
    }

    // Put the message into a notification and post it to notification drawer.
    private void sendNotificationToNotificationDrawer(Ride ride, String msg, String title, String extraMsg) {
        Log.i(TAG, msg);
        mNotificationManager = (android.app.NotificationManager)  context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = getPendingIntentForRide(ride);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setLargeIcon(((BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_launcher)).getBitmap())
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg +"\n"+ extraMsg))
                        .setContentText(msg)
                        .setDefaults(Notification.DEFAULT_ALL);

        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(index, mBuilder.build());

        // Send Notification to Extra connected devices
       // sendExtraNotifications(context.getResources().getString(R.string.messageTitle), msg, context);
    }

    /*
     * Method to handle any extra notification needs to be send on other devices like Pebble
     * Any business logic about turning notification on/off to specific device should be applied in this method
     */
    public void sendExtraNotifications(String title, String body, Context context){
        PebbleNotifications.sendNotificationToPebble(title, body, context);
    }

    public void updateNotifications(Bundle extras) {
        String rideID = extras.getString("ride_id");
        int rideId = -1;
        try {
            rideId = Integer.parseInt(rideID);
            Log.i(TAG, "Received for ride  " + rideId);
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }

        if (extras.getString("message") != null) {
            message = extras.getString("message");
            Log.i(TAG, "message: "+message);
        }
        if (extras.getString("title") != null) {
            title = extras.getString("title");
            Log.i(TAG, "title: "+title);
        }

        if(title.contains("cancel")) isDeletedRide = true;
        else isDeletedRide=false;

        if(rideId != -1) {
            Log.e(TAG, "FOUND rideID: " + rideID + " , rideId: " + rideId);
            Ride ride;
            if(!isDeletedRide) ride = ridesService.getRideSynchronous(rideId);
            else ride = ridesService.getDeletedRideSynchronous(rideId);
            if(ride!=null) {
                String extraMsg = getRideInfoString(ride);
                sendNotificationToNotificationDrawer(ride, message, title, extraMsg);
            }
            else {
                Log.e(TAG, "NOT_FOUND getRideSynchronous returned "+ride);
                showSimpleNotification(message, title);
            }
        }
        else {
            Log.e(TAG, "NOT_FOUND rideID: "+ rideID + " , rideId: "+rideId);
            showSimpleNotification(message, title);
        }
    }

    private String getRideInfoString(Ride ride) {

        String date = StringHelper.parseDate(ride.getDepartureTime());
        String time = StringHelper.parseTime(ride.getDepartureTime());

        String result = "FROM " + ride.getDeparturePlace() + " \nTO " + ride.getDestination() + " \non " + date + " at "+time;
        return result;
    }

    private void showSimpleNotification(String msg, String title) {
        Log.i(TAG, msg);
        mNotificationManager = (android.app.NotificationManager)  context.getSystemService(Context.NOTIFICATION_SERVICE);

        // if there is no ride_id in the bundle
        PendingIntent contentIntent = PendingIntent.getActivity(context, ++index, new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setLargeIcon(((BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_launcher)).getBitmap())
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg)
                        .setDefaults(Notification.DEFAULT_ALL);

        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(index, mBuilder.build());

        // Send Notification to Extra connected devices
       // sendExtraNotifications(title, msg, context);
    }
}
