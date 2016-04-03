package de.tum.mitfahr.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.CreateMessageEvent;
import de.tum.mitfahr.networking.clients.MessagesRESTClient;
import de.tum.mitfahr.networking.models.requests.MessageRequest;
import retrofit.client.Response;

/**
 * Created by amr on 07.07.14.
 */
public class MessagesService extends AbstractService{

    private MessagesRESTClient mMessagesRESTClient;
    private Bus mBus;
    private String userAPIKey;
    private int senderId;

    public MessagesService(final Context context) {
        super(context);
        String baseBackendURL = TUMitfahrApplication.getApplication(context).getBaseURLBackend();
        mBus = BusProvider.getInstance();
        mBus.register(this);
        mMessagesRESTClient = new MessagesRESTClient(baseBackendURL,context);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        senderId = mSharedPreferences.getInt("id", 0);
        userAPIKey = mSharedPreferences.getString("api_key", null);
    }

    public void createMessage(int rideId, int conversationId, int receiverId, String content) {
        MessageRequest message = new MessageRequest(senderId, receiverId, content);
        //mMessagesRESTClient.createMessage(userAPIKey, rideId, conversationId, message);
    }

    @Subscribe
    public void onCreateMessageResult(CreateMessageEvent result) {
        if (result.getType() == CreateMessageEvent.Type.RESULT) {
            Response retrofitResponse = result.getRetrofitResponse();
            if (200 == retrofitResponse.getStatus()) {
                mBus.post(new CreateMessageEvent(CreateMessageEvent.Type.SUCCESSFUL,retrofitResponse));
            } else {
                mBus.post(new CreateMessageEvent(CreateMessageEvent.Type.FAILED,retrofitResponse));
            }
        }
    }
}
