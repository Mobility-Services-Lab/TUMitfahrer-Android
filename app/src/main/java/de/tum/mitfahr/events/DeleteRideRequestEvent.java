package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.RidesResponse;
import retrofit.client.Response;

/**
 * Created by amr on 28/06/14.
 */
public class DeleteRideRequestEvent extends AbstractEvent {

    public enum Type
    {
        SUCCESSFUL,
        FAILED,
        RESULT
    }

    private Response retrofitResponse;

    public DeleteRideRequestEvent(Type type, Response retrofitResponse) {
        super(type);
        this.retrofitResponse = retrofitResponse;
    }

    public Response getRetrofitResponse() {
        return this.retrofitResponse;
    }
}
