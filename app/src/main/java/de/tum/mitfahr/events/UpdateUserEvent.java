package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.UpdateUserResponse;
import retrofit.client.Response;

/**
 * Created by amr on 22/06/14.
 */
public class UpdateUserEvent extends AbstractEvent {
    public enum Type
    {
        USER_UPDATED,
        UPDATE_FAILED,
        RESULT
    }

    private UpdateUserResponse updateUserResponse;
    private Response retrofitResponse;

    public UpdateUserEvent(Type type, UpdateUserResponse updateUserResponse, Response response) {
        super(type);
        this.updateUserResponse = updateUserResponse;
        this.retrofitResponse = response;
    }

    public UpdateUserResponse getUpdateUserResponse() {
        return this.updateUserResponse;
    }

    public Response getRetrofitResponse() {
        return this.retrofitResponse;
    }
}
