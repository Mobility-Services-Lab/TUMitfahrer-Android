package de.tum.mitfahr.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.SendFeedbackEvent;
import de.tum.mitfahr.networking.clients.FeedbackRESTClient;
import de.tum.mitfahr.networking.models.requests.FeedbackRequest;
import retrofit.client.Response;

/**
 * Created by amr on 07.07.14.
 */
public class FeedbackService extends AbstractService{

    private FeedbackRESTClient mFeedbackRESTClient;
    private Bus mBus;
    private String userAPIKey;
    private int userId;

    public FeedbackService(final Context context) {
        super(context);
        String baseBackendURL = TUMitfahrApplication.getApplication(context).getBaseURLBackend();
        mBus = BusProvider.getInstance();
        mBus.register(this);
        mFeedbackRESTClient = new FeedbackRESTClient(baseBackendURL,context);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        userId = mSharedPreferences.getInt("id", 0);
        userAPIKey = mSharedPreferences.getString("api_key", null);
    }

    public void sendFeedback(String title, String content) {

        FeedbackRequest feedback = new FeedbackRequest(title, content);
        mFeedbackRESTClient.sendFeedback(userAPIKey, feedback);
    }

    @Subscribe
    public void onSendFeedbackResult(SendFeedbackEvent result) {
        if (result.getType() == SendFeedbackEvent.Type.RESULT) {
            Response retrofitResponse = result.getRetrofitResponse();
            if (200 == retrofitResponse.getStatus()) {
                mBus.post(new SendFeedbackEvent(SendFeedbackEvent.Type.SUCCESSFUL,retrofitResponse));
            } else {
                mBus.post(new SendFeedbackEvent(SendFeedbackEvent.Type.FAILED,retrofitResponse));
            }
        }
    }
}
