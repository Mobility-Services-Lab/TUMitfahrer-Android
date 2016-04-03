package de.tum.mitfahr.events;

import retrofit.client.Response;

/**
 * Created by amr on 21/06/14.
 */
public class RejectRequestEvent extends AbstractEvent {

    public enum Type
    {
        REJECT_SENT,
        REJECT_FAILED,
        RESULT
    }

    private Response retrofitResponse;

    public RejectRequestEvent(Type type, Response retrofitResponse) {
        super(type);
        this.retrofitResponse = retrofitResponse;
    }

    public Response getRetrofitResponse() {
        return this.retrofitResponse;
    }
}
