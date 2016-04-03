package de.tum.mitfahr.events;

import retrofit.client.Response;

/**
 * Created by amr on 21/06/14.
 */
public class RespondToRequestEvent extends AbstractEvent {

    public enum Type
    {
        RESPOND_SENT,
        RESPOND_FAILED,
        RESULT
    }

    private Response retrofitResponse;

    public RespondToRequestEvent(Type type, Response retrofitResponse) {
        super(type);
        this.retrofitResponse = retrofitResponse;
    }

    public Response getRetrofitResponse() {
        return this.retrofitResponse;
    }
}
