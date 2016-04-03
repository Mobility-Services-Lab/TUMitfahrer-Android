package de.tum.mitfahr.events;

import retrofit.client.Response;

/**
 * Created by Duygu on 04/08/2015.
 */
public class SessionCheckEvent extends AbstractEvent {
    public enum Type
    {
        VALID,
        INVALID,
        RESULT
    }

    private Response response;

    public SessionCheckEvent(Type type, Response response) {
        super(type);
        this.response = response;
    }

    public Response getResponse() { return this.response; }
}
