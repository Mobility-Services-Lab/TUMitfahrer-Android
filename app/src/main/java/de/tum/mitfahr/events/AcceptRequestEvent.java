package de.tum.mitfahr.events;

import retrofit.client.Response;

/**
 * Created by amr on 21/06/14.
 */
public class AcceptRequestEvent extends AbstractEvent {

    public enum Type
    {
        ACCEPT_SENT,
        ACCEPT_FAILED,
        RESULT
    }

    private Response retrofitResponse;

    public AcceptRequestEvent(Type type, Response retrofitResponse) {
        super(type);
        this.retrofitResponse = retrofitResponse;
    }

    public Response getRetrofitResponse() {
        return this.retrofitResponse;
    }
}
