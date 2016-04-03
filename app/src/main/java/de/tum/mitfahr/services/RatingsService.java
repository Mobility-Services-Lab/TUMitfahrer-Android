package de.tum.mitfahr.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.GetUserRatingsEvent;
import de.tum.mitfahr.events.RateUserEvent;
import de.tum.mitfahr.networking.clients.RatingsRESTClient;
import retrofit.client.Response;

/**
 * Created by amr on 23/06/14.
 */
public class RatingsService extends AbstractService{

    private RatingsRESTClient mRatingsRESTClient;
    private Bus mBus;
    private String userAPIKey;

    public RatingsService(final Context context) {
        super(context);
        String baseBackendURL = TUMitfahrApplication.getApplication(context).getBaseURLBackend();
        mBus = BusProvider.getInstance();
        mBus.register(this);
        mRatingsRESTClient = new RatingsRESTClient(baseBackendURL,context);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        userAPIKey = mSharedPreferences.getString("api_key", null);
    }

    public void getUserRatings(int userId) {
        mRatingsRESTClient.getUserRatings(userAPIKey, userId);
    }

    @Subscribe
    public void onGetUserRatingsResult(GetUserRatingsEvent result) {
        if(result.getType() == GetUserRatingsEvent.Type.RESULT) {
            Response retrofitResponse = result.getRetrofitResponse();
            if (200 == retrofitResponse.getStatus()) {
                mBus.post(new GetUserRatingsEvent(GetUserRatingsEvent.Type.GET_SUCCESSFUL,
                        result.getRatingsResponse(), retrofitResponse));
            } else {
                mBus.post(new GetUserRatingsEvent(GetUserRatingsEvent.Type.GET_FAILED,
                        result.getRatingsResponse(), retrofitResponse));
            }
        }
    }

    public void rateUser(int toUserId, int rideId, int ratingType) {
        int fromUserId = mSharedPreferences.getInt("id", 0);
        mRatingsRESTClient.rateUser(userAPIKey, fromUserId, toUserId, rideId, ratingType);
    }

    @Subscribe
    public void onRateUserResult(RateUserEvent result) {
        if(result.getType() == RateUserEvent.Type.RESULT) {
            Response retrofitResponse = result.getRetrofitResponse();
            if (201 == retrofitResponse.getStatus() &&
                    result.getRatingResponse().getRating() != null) {
                mBus.post(new RateUserEvent(RateUserEvent.Type.SUCCESSFUL,
                        result.getRatingResponse(), retrofitResponse));
            } else {
                mBus.post(new RateUserEvent(RateUserEvent.Type.FAILED,
                        result.getRatingResponse(), retrofitResponse));
            }
        }
    }
}
