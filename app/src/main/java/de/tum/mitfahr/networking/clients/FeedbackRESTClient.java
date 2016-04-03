package de.tum.mitfahr.networking.clients;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import de.tum.mitfahr.events.SendFeedbackEvent;
import de.tum.mitfahr.networking.api.FeedbackAPIService;
import de.tum.mitfahr.networking.events.RequestFailedEvent;
import de.tum.mitfahr.networking.models.requests.FeedbackRequest;
import de.tum.mitfahr.networking.models.response.FeedbackResponse;
import de.tum.mitfahr.networking.models.response.StatusResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by amr on 07.07.14.
 */
public class FeedbackRESTClient extends AbstractRESTClient {

    private FeedbackAPIService feedbackAPIService;
    private Callback sendFeedbackCallback = new Callback<FeedbackResponse>() {

        @Override
        public void success(FeedbackResponse feedbackResponse, Response response) {
            Log.d("Feedback Response", response.getStatus()+"");
            if (response.getStatus() == 200)
                mBus.post(new SendFeedbackEvent(SendFeedbackEvent.Type.SUCCESSFUL, response));
            else
                mBus.post(new SendFeedbackEvent(SendFeedbackEvent.Type.FAILED, response));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            Log.d("Feedback failure", retrofitError.getResponse()+"");
            mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,retrofitError));
        }
    };

    public FeedbackRESTClient(String mBaseBackendURL,Context context) {
        super(mBaseBackendURL,context);
        feedbackAPIService = mRestAdapter.create(FeedbackAPIService.class);
    }

    public void sendFeedback(String userAPIKey, FeedbackRequest feedback) {
       // feedbackAPIService.getStatus(statusCallback);
        feedbackAPIService.sendFeedback(userAPIKey, feedback, sendFeedbackCallback);
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
}
