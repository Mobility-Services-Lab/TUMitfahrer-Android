package de.tum.mitfahr.networking.clients;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import de.tum.mitfahr.events.GetConversationEvent;
import de.tum.mitfahr.events.GetConversationsEvent;
import de.tum.mitfahr.networking.api.ConversationsAPIService;
import de.tum.mitfahr.networking.events.RequestFailedEvent;
import de.tum.mitfahr.networking.models.response.ConversationResponse;
import de.tum.mitfahr.networking.models.response.ConversationsResponse;
import de.tum.mitfahr.networking.models.response.StatusResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by amr on 07.07.14.
 */
public class ConversationsRESTClient extends AbstractRESTClient {

    private ConversationsAPIService conversationsAPIService;

    public ConversationsRESTClient(String mBaseBackendURL, Context context) {

        super(mBaseBackendURL,context);
        conversationsAPIService = mRestAdapter.create(ConversationsAPIService.class);
    }

    public void getConversations(String userAPIKey, int rideId) {
       // conversationsAPIService.getStatus(statusCallback);
        conversationsAPIService.getConversations(userAPIKey, rideId, getConversationsCallback);
    }

    public void getConversation(String userAPIKey, int rideId, int conversationId) {
      //  conversationsAPIService.getStatus(statusCallback);
        conversationsAPIService.getConversation(userAPIKey, rideId, conversationId,
                getConversationCallback);
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

    private Callback<ConversationsResponse> getConversationsCallback = new Callback<ConversationsResponse>() {
        @Override
        public void success(ConversationsResponse conversationsResponse, Response response) {
            mBus.post(new GetConversationsEvent(GetConversationsEvent.Type.RESULT,
                    conversationsResponse, response));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,retrofitError));
        }
    };

    private Callback<ConversationResponse> getConversationCallback = new Callback<ConversationResponse>() {
        @Override
        public void success(ConversationResponse conversationResponse, Response response) {
            mBus.post(new GetConversationEvent(GetConversationEvent.Type.RESULT,
                    conversationResponse, response));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,retrofitError));
        }
    };

}
