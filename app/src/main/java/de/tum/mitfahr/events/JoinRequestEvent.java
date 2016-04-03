package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.JoinRequestResponse;
import retrofit.client.Response;

/**
 * Created by amr on 21/06/14.
 */
public class JoinRequestEvent extends AbstractEvent {

    public enum Type
    {
        REQUEST_SENT,
        REQUEST_FAILED,
        RESULT
    }

    private JoinRequestResponse joinRequestResponse;
    private Response retrofitResponse;

    public JoinRequestEvent(Type type, JoinRequestResponse joinRequestResponse, Response response) {
        super(type);
        this.joinRequestResponse = joinRequestResponse;
        this.retrofitResponse = response;
    }

    public JoinRequestResponse getJoinRequestResponse() {
        return this.joinRequestResponse;
    }

    public Response getRetrofitResponse() {
        return this.retrofitResponse;
    }

}
