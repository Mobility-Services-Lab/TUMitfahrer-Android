package de.tum.mitfahr;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import de.tum.mitfahr.events.GetDepartmentEvent;
import de.tum.mitfahr.networking.events.RequestFailedEvent;
import de.tum.mitfahr.networking.models.Department;
import de.tum.mitfahr.networking.panoramio.PanoramioService;
import de.tum.mitfahr.services.ActivitiesService;
import de.tum.mitfahr.services.ConversationsService;
import de.tum.mitfahr.services.FeedbackService;
import de.tum.mitfahr.services.MessagesService;
import de.tum.mitfahr.services.ProfileService;
import de.tum.mitfahr.services.RatingsService;
import de.tum.mitfahr.services.RidesService;
import de.tum.mitfahr.services.SearchService;
import de.tum.mitfahr.services.StatusService;
import retrofit.RetrofitError;

/**
 * Created by abhijith on 09/05/14.
 */
public class TUMitfahrApplication extends Application {

    private static final String BASE_BACKEND_URL = ""; //Insert the Backend Url of Live Server here.
    private static final String PANORAMIO_BACKEND_URL = "http://www.panoramio.com/map";

    private Bus mBus = BusProvider.getInstance();

    private static TUMitfahrApplication mInstance;

    private ProfileService mProfileService;
    private RidesService mRidesService;
    private SearchService mSearchService;
    private Context mContext;
    private ActivitiesService mActivitiesService;
    private MessagesService mMessagesService;
    private ConversationsService mConversationsService;
    private FeedbackService mFeedbackService;
    private RatingsService mRatingsService;
    private PanoramioService mPanoramioService;
    private StatusService mStatusService;
    private SharedPreferences sharedPreferences;
    private ArrayList<Department> departments = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        mBus.register(this);
        mProfileService = new ProfileService(this);
        mRidesService = new RidesService(this);
        mSearchService = new SearchService(this);
        mActivitiesService = new ActivitiesService(this);
        mPanoramioService = new PanoramioService(this);
        mFeedbackService = new FeedbackService(this);
        mContext = this;
        departments = new ArrayList<>();

        getDepartmentsFromBackend();
    }

    public static synchronized TUMitfahrApplication getInstance() {
        return mInstance;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                            .setDescription(
                                    new StandardExceptionParser(this, null)
                                            .getDescription(Thread.currentThread().getName(), e))
                            .setFatal(false)
                            .build()
            );
        }
    }

    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }



    @Override
    public void onTerminate (){
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

    private static final class MyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {

        public void onActivityCreated(Activity activity, Bundle bundle) {

            final Activity activityOne = activity;
            final Context mContext = activity.getBaseContext();

          //  StatusService mStatusService;
            Log.e("","onActivityCreated:" + activity.getLocalClassName());
        }

        public void onActivityDestroyed(Activity activity) {
            Log.e("","onActivityDestroyed:" + activity.getLocalClassName());
        }

        public void onActivityPaused(Activity activity) {
            Log.e("", "onActivityPaused:" + activity.getLocalClassName());
        }

        public void onActivityResumed(Activity activity) {

            final Activity activityOne = activity;
            final Context mContext = activity.getBaseContext();

            StatusService mStatusService;
            mStatusService = new StatusService(mContext,activityOne);
            Log.e("","onActivityResumed:" + activity.getLocalClassName());
        }

        public void onActivitySaveInstanceState(Activity activity,
                                                Bundle outState) {
            Log.e("","onActivitySaveInstanceState:" + activity.getLocalClassName());
        }

        public void onActivityStarted(Activity activity) {

            Log.e("","onActivityStarted:" + activity.getLocalClassName());
        }

        public void onActivityStopped(Activity activity) {
            Log.e("","onActivityStopped:" + activity.getLocalClassName());
        }
    }

    @Subscribe
    public void onGetDepartments(GetDepartmentEvent event){
        if (event.getType() == GetDepartmentEvent.Type.GET_SUCCESSFUL) {
            departments = event.getResponse().getDepartments();
        }
    }

    public ProfileService getProfileService() {
        return mProfileService;
    }

    public RidesService getRidesService() {
        return mRidesService;
    }

    public SearchService getSearchService() {
        return mSearchService;
    }

    public ActivitiesService getActivitiesService() {
        return mActivitiesService;
    }

    public MessagesService getMessagesService(){
        return mMessagesService;
    }

    public ConversationsService getConversationsService(){
        return mConversationsService;
    }

    public FeedbackService getFeedbackService(){
        return mFeedbackService;
    }

    public RatingsService getRatingsService(){
        return mRatingsService;
    }

    public PanoramioService getPanoramioService() {
        return mPanoramioService;
    }

    public String getBaseURLBackend() {
        return BASE_BACKEND_URL;
    }

    public String getPanoramioURLBackend() {
        return PANORAMIO_BACKEND_URL;
    }

    public static TUMitfahrApplication getApplication(final Context context) {
        return (TUMitfahrApplication) context.getApplicationContext();
    }

    public ArrayList<Department> getDepartments(){
        return departments;
    }

    public void getDepartmentsFromBackend(){
        if(departments.isEmpty()){
            this.getProfileService().getDepartments();
            if (!departments.isEmpty()) {
                registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());
            }
        }
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Subscribe
    public void onConnectionError(RequestFailedEvent event) {
        if(event.getType() == RequestFailedEvent.Type.ERROR_404) {
            Toast.makeText(this, "Trouble connecting to the server.", Toast.LENGTH_LONG).show();
        }
        else if(event.getType() == RequestFailedEvent.Type.ERROR_401) {
            Toast.makeText(this, "Your session has expired. Please login again.", Toast.LENGTH_LONG).show();
            //TODO: possibly redirect to login activity?
        }
        else if(event.getError().getKind() == RetrofitError.Kind.NETWORK)
            Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_LONG).show();
    }
}
