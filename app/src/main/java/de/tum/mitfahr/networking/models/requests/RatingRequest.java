package de.tum.mitfahr.networking.models.requests;

/**
 * Created by amr on 29/06/14.
 */
public class RatingRequest {

    private int toUserId;
    private int rideType;
    private int ratingType;

    public RatingRequest(int toUserId, int rideType, int ratingType) {
        this.toUserId = toUserId;
        this.rideType = rideType;
        this.ratingType = ratingType;
    }
}
