package de.tum.mitfahr.networking.models.response;

import java.util.ArrayList;

import de.tum.mitfahr.networking.models.Ride;

/**
 * Created by amr on 18/05/14.
 */
public class OfferRideResponse {

    private String status;
    private String message;
    private ArrayList<Ride> rides;
    private Ride ride;
    private String[] errors;

    public OfferRideResponse(String status, String message, ArrayList<Ride> rides, Ride ride) {
        this.status = status;
        this.message = message;
        this.rides = rides;
        this.ride = ride;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Ride> getRides() {
        return rides;
    }

    public Ride getRide() {
        return ride;
    }

    public String[] getErrors() {
        return  errors;
    }

}
