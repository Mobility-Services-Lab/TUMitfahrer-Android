package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.ConversationsResponse;
import retrofit.client.Response;

/**
 * Created by amr on 07.07.14.
 */
public class GetConversationsEvent extends AbstractEvent {

    public enum Type
    {
        GET_SUCCESSFUL,
        GET_FAILED,
        RESULT
    }

    private ConversationsResponse mConversationsResponse;
    private Response retrofitResponse;

    public GetConversationsEvent(Type type, ConversationsResponse conversationsResponse, Response retrofitResponse) {
        super(type);
        this.mConversationsResponse = conversationsResponse;
        this.retrofitResponse = retrofitResponse;
    }

    public ConversationsResponse getConversationsResponse() {
        return this.mConversationsResponse;
    }

    public Response getRetrofitResponse() { return this.getRetrofitResponse(); }
}
