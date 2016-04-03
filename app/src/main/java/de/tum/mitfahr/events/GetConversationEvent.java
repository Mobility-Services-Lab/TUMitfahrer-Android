package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.ConversationResponse;
import retrofit.client.Response;

/**
 * Created by amr on 07.07.14.
 */
public class GetConversationEvent extends AbstractEvent {

    public enum Type
    {
        GET_SUCCESSFUL,
        GET_FAILED,
        RESULT
    }

    private ConversationResponse mConversationResponse;
    private Response retrofitResponse;

    public GetConversationEvent(Type type, ConversationResponse conversationResponse, Response retrofitResponse) {
        super(type);
        this.mConversationResponse = conversationResponse;
        this.retrofitResponse = retrofitResponse;
    }

    public ConversationResponse getConversationResponse() {
        return this.mConversationResponse;
    }

    public Response getRetrofitResponse() { return this.getRetrofitResponse(); }
}
