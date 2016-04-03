package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.GetUserResponse;
import retrofit.client.Response;

/**
 * Created by amr on 22/06/14.
 */
public class GetUserEvent extends AbstractEvent {
    public enum Type
    {
        GET_SUCCESSFUL,
        GET_FAILED,
        RESULT
    }

    private GetUserResponse getUserResponse;
    private Response retrofitResponse;

    public GetUserEvent(Type type, GetUserResponse getUserResponse, Response response) {
        super(type);
        this.getUserResponse = getUserResponse;
        this.retrofitResponse = response;
    }

    public GetUserResponse getGetUserResponse() {
        return this.getUserResponse;
    }

    public Response getRetrofitResponse() {
        return this.retrofitResponse;
    }
}
