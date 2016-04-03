package de.tum.mitfahr.events;

import retrofit.client.Response;

/**
 * Created by Duygu on 13/07/2015.
 */
public class LeaveRideEvent extends AbstractEvent {

    public enum Type
    {
        SUCCESSFUL,
        FAILED,
        RESULT
    }

    private Response retrofitResponse;

    public LeaveRideEvent(Type type, Response retrofitResponse) {
        super(type);
        this.retrofitResponse = retrofitResponse;
    }

    public Response getRetrofitResponse() {
        return this.retrofitResponse;
    }

}
