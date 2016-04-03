package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.RatingsResponse;
import retrofit.client.Response;

/**
 * Created by amr on 29/06/14.
 */
public class GetUserRatingsEvent extends AbstractEvent{
    public enum Type
    {
        GET_SUCCESSFUL,
        GET_FAILED,
        RESULT
    }

    private RatingsResponse mRatingsResponse;
    private Response mRetrofitResponse;

    public GetUserRatingsEvent(Type type, RatingsResponse ratingsResponse, Response response) {
        super(type);
        this.mRatingsResponse = ratingsResponse;
        this.mRetrofitResponse = response;
    }

    public RatingsResponse getRatingsResponse() {
        return this.mRatingsResponse;
    }

    public Response getRetrofitResponse() { return this.mRetrofitResponse; }
}
