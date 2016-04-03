package de.tum.mitfahr.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.GetConversationEvent;
import de.tum.mitfahr.events.GetConversationsEvent;
import de.tum.mitfahr.networking.clients.ConversationsRESTClient;
import de.tum.mitfahr.networking.models.response.ConversationResponse;
import de.tum.mitfahr.networking.models.response.ConversationsResponse;

/**
 * Created by amr on 07.07.14.
 */
public class ConversationsService extends AbstractService{

    private ConversationsRESTClient mConversationsRESTClient;
    private Bus mBus;
    private String userAPIKey;
    private int userId;

    public ConversationsService(final Context context) {
        super(context);
        String baseBackendURL = TUMitfahrApplication.getApplication(context).getBaseURLBackend();
        mBus = BusProvider.getInstance();
        mBus.register(this);
        mConversationsRESTClient = new ConversationsRESTClient(baseBackendURL,context);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        userId = mSharedPreferences.getInt("id", 0);
        userAPIKey = mSharedPreferences.getString("api_key", null);
    }

    public void getConversations(int rideId) {
        mConversationsRESTClient.getConversations(userAPIKey, rideId);
    }

    @Subscribe
    public void onGetConversationsResult(GetConversationsEvent result) {
        if(result.getType() == GetConversationsEvent.Type.RESULT) {
            ConversationsResponse response = result.getConversationsResponse();
            if (null == response.getConversations()) {
                mBus.post(new GetConversationsEvent(GetConversationsEvent.Type.GET_FAILED,
                        response, result.getRetrofitResponse()));
            } else {
                mBus.post(new GetConversationsEvent(GetConversationsEvent.Type.GET_SUCCESSFUL,
                        response, result.getRetrofitResponse()));
            }
        }
    }

    public void getConversation(int rideId, int conversationId) {
        mConversationsRESTClient.getConversation(userAPIKey, rideId, conversationId);
    }

    @Subscribe
    public void onGetConversationResult(GetConversationEvent result) {
        if(result.getType() == GetConversationEvent.Type.RESULT) {
            ConversationResponse response = result.getConversationResponse();
            if (null == response.getConversation()) {
                mBus.post(new GetConversationEvent(GetConversationEvent.Type.GET_FAILED,
                        response, result.getRetrofitResponse()));
            } else {
                mBus.post(new GetConversationEvent(GetConversationEvent.Type.GET_SUCCESSFUL,
                        response, result.getRetrofitResponse()));
            }
        }
    }
}
