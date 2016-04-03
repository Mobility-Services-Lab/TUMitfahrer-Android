package de.tum.mitfahr.networking.models;

/**
 * Created by amr on 29/06/14.
 */
public class Rating {

    private int ratingType;
    private int fromUserId;
    private int toUserId;
    private int rideId;
    private String createdAt;
    private String updatedAt;

    public Rating(int ratingType, int fromUserId, int toUserId, int rideId, String createdAt, String updatedAt) {
        this.ratingType = ratingType;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.rideId = rideId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getRatingType() {
        return ratingType;
    }

    public void setRatingType(int ratingType) {
        this.ratingType = ratingType;
    }

    public int getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(int fromUserId) {
        this.fromUserId = fromUserId;
    }

    public int getToUserId() {
        return toUserId;
    }

    public void setToUserId(int toUserId) {
        this.toUserId = toUserId;
    }

    public int getRideId() {
        return rideId;
    }

    public void setRideId(int rideId) {
        this.rideId = rideId;
    }
}
