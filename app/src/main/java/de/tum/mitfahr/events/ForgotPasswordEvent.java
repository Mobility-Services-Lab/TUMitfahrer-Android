package de.tum.mitfahr.events;

import retrofit.client.Response;

/**
 * Created by amr on 23/06/14.
 */
public class ForgotPasswordEvent extends AbstractEvent {
    private Response retrofitResponse;

    public enum Type {
        RESULT,
        SUCCESS,
        FAIL,
        NOT_USER
    }

    public ForgotPasswordEvent(Type type, Response response) {
        super(type);
        this.retrofitResponse = response;
    }

    public Response getRetrofitResponse() {
        return this.retrofitResponse;
    }
}
