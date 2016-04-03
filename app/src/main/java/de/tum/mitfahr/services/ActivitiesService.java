package de.tum.mitfahr.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.GetActivitiesEvent;
import de.tum.mitfahr.events.GetBadgesEvent;
import de.tum.mitfahr.networking.clients.ActivitiesRESTClient;
import de.tum.mitfahr.networking.models.response.ActivitiesResponse;
import de.tum.mitfahr.networking.models.response.BadgesResponse;

/**
 * Created by amr on 02/07/14.
 */
public class ActivitiesService extends AbstractService {

    private ActivitiesRESTClient mActivitiesRESTClient;
    private Bus mBus;
    private String userAPIKey;
    private int userId;

    public ActivitiesService(final Context context) {
        super(context);
        String baseBackendURL = TUMitfahrApplication.getApplication(context).getBaseURLBackend();
        mBus = BusProvider.getInstance();
        mBus.register(this);
        mActivitiesRESTClient = new ActivitiesRESTClient(baseBackendURL,context);
        userId = mSharedPreferences.getInt("id", 0);
        userAPIKey = mSharedPreferences.getString("api_key", null);
    }

    public void getActivities() {
        mActivitiesRESTClient.getActivities(userAPIKey);
    }

    @Subscribe
    public void onGetActivitiesResult(GetActivitiesEvent result) {
        if (result.getType() == GetActivitiesEvent.Type.RESULT) {
            ActivitiesResponse response = result.getResponse();
            if ((null == response) || (null == response.getActivities())) {
            //if (result.getRetrofitResponse().getStatus() != 200) {
                mBus.post(new GetActivitiesEvent(GetActivitiesEvent.Type.GET_FAILED, response,
                        result.getRetrofitResponse()));
                //Log.e("ActivitiesService", "getActivities null fail " + response.getStatus() + "");
                //Log.e("ActivitiesService", "getActivities null fail " + response.getActivities());
            } else {
                    mBus.post(new GetActivitiesEvent(GetActivitiesEvent.Type.GET_SUCCESSFUL, response,
                            result.getRetrofitResponse()));
                    //Log.e("ActivitiesService", "getActivities success " + response.getStatus() + "");
                    //Log.e("ActivitiesService", "getActivities success " + response.getActivities());

            }
        }
    }

    public void getBadges(String campusUpdatedAt, String activityUpdatedAt,
                          String timelineUpdatedAt, String myRidesUpdatedAt, int userId) {
        mActivitiesRESTClient.getBadges(userAPIKey, campusUpdatedAt, activityUpdatedAt, timelineUpdatedAt,
                myRidesUpdatedAt, userId);
    }

    @Subscribe
    public void onGetBadgesResult(GetBadgesEvent result) {
        if (result.getType() == GetBadgesEvent.Type.RESULT) {
            BadgesResponse response = result.getResponse();
            if (null == response.getBadgeCounter()) {
                mBus.post(new GetBadgesEvent(GetBadgesEvent.Type.GET_FAILED, response));
            } else {
                mBus.post(new GetBadgesEvent(GetBadgesEvent.Type.GET_SUCCESSFUL, response));
            }
        }
    }


}
