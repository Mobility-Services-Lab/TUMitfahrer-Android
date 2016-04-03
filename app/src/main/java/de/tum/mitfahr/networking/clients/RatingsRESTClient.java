package de.tum.mitfahr.networking.clients;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import de.tum.mitfahr.events.GetUserRatingsEvent;
import de.tum.mitfahr.events.RateUserEvent;
import de.tum.mitfahr.networking.api.RatingsAPIService;
import de.tum.mitfahr.networking.events.RequestFailedEvent;
import de.tum.mitfahr.networking.models.requests.RatingRequest;
import de.tum.mitfahr.networking.models.response.RatingResponse;
import de.tum.mitfahr.networking.models.response.RatingsResponse;
import de.tum.mitfahr.networking.models.response.StatusResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by amr on 23/06/14.
 */
public class RatingsRESTClient extends AbstractRESTClient {

    private RatingsAPIService ratingsAPIService;

    public RatingsRESTClient(String mBaseBackendURL,Context context) {

        super(mBaseBackendURL,context);
        ratingsAPIService = mRestAdapter.create(RatingsAPIService.class);
    }

    public void getUserRatings(String userAPIKey, int userId) {
       // ratingsAPIService.getStatus(statusCallback);
        ratingsAPIService.getUserRatings(userAPIKey, userId, getUserRatingsCallback);
    }

//    private Callback<StatusResponse> statusCallback = new Callback<StatusResponse>() {
//        @Override
//        public void success(StatusResponse statusResponse, Response response) {
//            Log.e("ActivitiesRESTClient", "URL Active: " + statusResponse.getStatus() + "");
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

    private Callback<RatingsResponse> getUserRatingsCallback = new Callback<RatingsResponse>() {
        @Override
        public void success(RatingsResponse ratingsResponse, Response response) {
            mBus.post(new GetUserRatingsEvent(GetUserRatingsEvent.Type.GET_SUCCESSFUL, ratingsResponse, response));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR, error));
        }
    };

    public void rateUser(String userAPIKey, int fromUserId, int toUserId, int rideType, int ratingType) {
      //  ratingsAPIService.getStatus(statusCallback);
        ratingsAPIService.rateUser(userAPIKey, fromUserId,
                new RatingRequest(toUserId, rideType, ratingType), rateUserCallback);
    }

    private Callback<RatingResponse> rateUserCallback = new Callback<RatingResponse>() {
        @Override
        public void success(RatingResponse ratingResponse, Response response) {
            mBus.post(new RateUserEvent(RateUserEvent.Type.RESULT, ratingResponse, response));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));
        }
    };
}
