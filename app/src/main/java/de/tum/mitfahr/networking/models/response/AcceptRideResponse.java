package de.tum.mitfahr.networking.models.response;

import java.util.ArrayList;

import de.tum.mitfahr.networking.models.Ride;

/**
 * Created by amr on 18/05/14.
 */
public class AcceptRideResponse {

    private String message;

    public AcceptRideResponse(String status, String message, ArrayList<Ride> rides, Ride ride) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
