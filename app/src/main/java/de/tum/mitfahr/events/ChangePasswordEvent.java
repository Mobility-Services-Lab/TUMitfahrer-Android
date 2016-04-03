package de.tum.mitfahr.events;

import retrofit.client.Response;

/**
 * Created by Duygu on 23.6.2015.
 */
public class ChangePasswordEvent extends AbstractEvent {

    private Response retrofitResponse;

    public enum Type {
        RESULT,
        SUCCESS,
        FAIL
    }

    public ChangePasswordEvent(Type type, Response response) {
        super(type);
        this.retrofitResponse = response;
    }

    public Response getRetrofitResponse() {
        return this.retrofitResponse;
    }
}
