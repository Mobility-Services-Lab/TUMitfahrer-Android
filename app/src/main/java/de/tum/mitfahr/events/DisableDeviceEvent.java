package de.tum.mitfahr.events;

import retrofit.client.Response;

/**
 * Created by Duygu on 15/07/2015.
 */
public class DisableDeviceEvent extends AbstractEvent {

    public enum Type
    {
        DISABLE_SUCCESFUL,
        DISABLE_FAILED,
        RESULT
    }

    private Response retrofitResponse;

    public DisableDeviceEvent(Type type, Response retrofitResponse) {
        super(type);
        this.retrofitResponse = retrofitResponse;
    }

    public Response getRetrofitResponse() {
        return this.retrofitResponse;
    }
}
