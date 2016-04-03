package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.DeleteRideResponse;
import retrofit.client.Response;

/**
 * Created by amr on 15/06/14.
 */
public class DeleteRideEvent extends AbstractEvent {

    public enum Type
    {
        DELETE_SUCCESSFUL,
        DELETE_FAILED,
        RESULT
    }

    private DeleteRideResponse mDeleteRideResponse;
    private Response retrofitResponse;

    public DeleteRideEvent(Type type, DeleteRideResponse deleteRideResponse, Response retrofitResponse) {
        super(type);
        this.mDeleteRideResponse = deleteRideResponse;
        this.retrofitResponse = retrofitResponse;
    }

    public DeleteRideResponse getResponse() {
        return this.mDeleteRideResponse;
    }

    public Response getRetrofitResponse() { return this.retrofitResponse; }
}
