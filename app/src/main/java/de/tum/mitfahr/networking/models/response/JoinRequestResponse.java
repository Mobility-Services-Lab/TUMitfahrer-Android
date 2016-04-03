package de.tum.mitfahr.networking.models.response;

import java.util.ArrayList;

import de.tum.mitfahr.networking.models.RideRequest;

/**
 * Created by amr on 21/06/14.
 */
public class JoinRequestResponse {
    private String status;
    private String message;
    private RideRequest rideRequest;

    public JoinRequestResponse(String status, String message, RideRequest rideRequest) {
        this.status = status;
        this.message = message;
        this.rideRequest = rideRequest;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public RideRequest getRideRequest() { return this.rideRequest; }


}
