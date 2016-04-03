package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.RatingResponse;
import retrofit.client.Response;

/**
 * Created by amr on 29/06/14.
 */
public class RateUserEvent extends AbstractEvent {

    public enum Type
    {
        SUCCESSFUL,
        FAILED,
        RESULT
    }

    private RatingResponse mRatingResponse;
    private Response mRetrofitResponse;

    public RateUserEvent(Type type, RatingResponse ratingResponse, Response response) {
        super(type);
        this.mRatingResponse = ratingResponse;
        this.mRetrofitResponse = response;
    }

    public RatingResponse getRatingResponse() {
        return this.mRatingResponse;
    }

    public Response getRetrofitResponse() { return this.mRetrofitResponse; }
}
