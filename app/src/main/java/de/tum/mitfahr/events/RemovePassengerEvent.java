package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.RidesResponse;
import retrofit.client.Response;

/**
 * Created by amr on 28/06/14.
 */
public class RemovePassengerEvent extends AbstractEvent {

    public enum Type
    {
        SUCCESSFUL,
        FAILED,
        RESULT
    }

    Response mRetrofitResponse;

    public RemovePassengerEvent(Type type, Response response) {
        super(type);
        this.mRetrofitResponse = response;
    }

    public Response getRetrofitResponse() { return  this.mRetrofitResponse; }
}
