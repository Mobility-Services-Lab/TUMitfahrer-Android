package de.tum.mitfahr.networking.clients;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.preference.PreferenceManager;
import android.util.Log;

import de.tum.mitfahr.events.GetActivitiesEvent;
import de.tum.mitfahr.events.GetBadgesEvent;
import de.tum.mitfahr.events.GetDepartmentEvent;
import de.tum.mitfahr.networking.api.ActivitiesAPIService;
import de.tum.mitfahr.networking.events.RequestFailedEvent;
import de.tum.mitfahr.networking.models.response.ActivitiesResponse;
import de.tum.mitfahr.networking.models.response.BadgesResponse;
import de.tum.mitfahr.networking.models.response.DepartmentResponse;
import de.tum.mitfahr.networking.models.response.StatusResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by amr on 02/07/14.
 */

public class ActivitiesRESTClient extends AbstractRESTClient {

    private ActivitiesAPIService activitiesAPIService;
    SharedPreferences mSharedPreferences;

    public ActivitiesRESTClient(String mBaseBackendURL, Context context) {
        super(mBaseBackendURL,context);
        this.activitiesAPIService = mRestAdapter.create(ActivitiesAPIService.class);
    }

    public void getActivities(String userAPIKey) {

//        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = mSharedPreferences.edit();
//        editor.putBoolean("oldAPI",false);
//        editor.commit();

       // activitiesAPIService.getStatus(statusCallback);
        activitiesAPIService.getActivities(userAPIKey, getActivitiesCallback);
    }

//    private Callback<StatusResponse> statusCallback = new Callback<StatusResponse>() {
//        @Override
//        public void success(StatusResponse statusResponse, Response response) {
//            Log.e("ActivitiesRESTClient", "URL Active: "+statusResponse.getStatus()+"");
//        }
//
//        @Override
//        public void failure(RetrofitError error) {
//
//            Log.e("ActivitiesRESTClient", "URL not active: "+error.getResponse().getReason()+"");
//
//            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean("oldAPI",true);
//            editor.commit();
//
//        }
//    };

    private Callback<ActivitiesResponse> getActivitiesCallback = new Callback<ActivitiesResponse>() {
        @Override
        public void success(ActivitiesResponse activitiesResponse, Response response) {
            mBus.post(new GetActivitiesEvent(GetActivitiesEvent.Type.RESULT,
                    activitiesResponse, response));
            Log.e("ActivitiesRESTClient", "getActivities success "+response.getStatus()+"");
            Log.e("ActivitiesRESTClient", "getActivities success " + response.getReason());
        }

        @Override
        public void failure(RetrofitError error) {
            handleErrorResponse(error);
            mBus.post(new GetActivitiesEvent(GetActivitiesEvent.Type.RESULT, null, null));
        }
    };

    public void getBadges(String userAPIKey, String campusUpdatedAt, String activityUpdatedAt,
                          String timelineUpdatedAt, String myRidesUpdatedAt, int userId) {
        activitiesAPIService.getBadges(userAPIKey, campusUpdatedAt, activityUpdatedAt,
                timelineUpdatedAt, myRidesUpdatedAt, userId, getBadgesCallback);
    }

    private Callback<BadgesResponse> getBadgesCallback = new Callback<BadgesResponse>() {
        @Override
        public void success(BadgesResponse badgesResponse, Response response) {
            mBus.post(new GetBadgesEvent(GetBadgesEvent.Type.RESULT, badgesResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));
        }
    };
}
