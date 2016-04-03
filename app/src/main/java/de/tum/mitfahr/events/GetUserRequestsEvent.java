package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.RequestsResponse;

/**
 * Created by amr on 28/06/14.
 */
public class GetUserRequestsEvent extends AbstractEvent {

    public enum Type
    {
        GET_SUCCESSFUL,
        GET_FAILED,
        RESULT
    }

    private RequestsResponse mRequestsResponse;

    public GetUserRequestsEvent(Type type, RequestsResponse requestsResponse) {
        super(type);
        this.mRequestsResponse = requestsResponse;
    }

    public RequestsResponse getResponse() {
        return this.mRequestsResponse;
    }
}
