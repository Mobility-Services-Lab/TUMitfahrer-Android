package de.tum.mitfahr.networking.models.requests;

/**
 * Author: abhijith
 * Date: 27/02/15.
 */
public class AcceptRideRequest {
    int passengerId;
    boolean confirmed;


    public AcceptRideRequest(int passengerId, boolean confirmed) {
        this.passengerId = passengerId;
        this.confirmed = confirmed;
    }
}
