package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.RequestsResponse;
import de.tum.mitfahr.networking.models.response.RidesResponse;

/**
 * Created by amr on 28/06/14.
 */
public class GetRideRequestsEvent extends AbstractEvent {

    public enum Type
    {
        GET_SUCCESSFUL,
        GET_FAILED,
        RESULT
    }

    private RequestsResponse mRequestsResponse;

    public GetRideRequestsEvent(Type type, RequestsResponse requestsResponse) {
        super(type);
        this.mRequestsResponse = requestsResponse;
    }

    public RequestsResponse getResponse() {
        return this.mRequestsResponse;
    }
}
