package de.tum.mitfahr.networking.clients;

import android.content.Context;

import de.tum.mitfahr.networking.api.MessagesAPIService;
import de.tum.mitfahr.networking.models.requests.MessageRequest;

/**
 * Created by amr on 07.07.14.
 */
public class MessagesRESTClient extends AbstractRESTClient {

    private MessagesAPIService messagesAPIService;

    public MessagesRESTClient(String mBaseBackendURL,Context context) {

        super(mBaseBackendURL,context);
        messagesAPIService = mRestAdapter.create(MessagesAPIService.class);
    }

//    public void createMessage(String userAPIKey, int rideId, int conversationId,
//                              MessageRequest message) {
//        messagesAPIService.createMessage(userAPIKey, rideId, conversationId, message,
//                createMessageCallback);
//    }
}
