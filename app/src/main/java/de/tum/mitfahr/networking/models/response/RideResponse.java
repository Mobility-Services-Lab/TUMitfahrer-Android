package de.tum.mitfahr.networking.models.response;

import java.util.ArrayList;

import de.tum.mitfahr.networking.models.Ride;

/**
 * Created by amr on 23/06/14.
 */
public class RideResponse {
    private String status;
    private String message;
    private Ride ride;

    public RideResponse(String status, String message, Ride ride) {
        this.status = status;
        this.message = message;
        this.ride = ride;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Ride getRide() {
        return ride;
    }
}
