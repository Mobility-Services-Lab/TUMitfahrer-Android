package de.tum.mitfahr.networking.models.response;

import java.util.ArrayList;

import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.RideRequest;

/**
 * Created by amr on 28/06/14.
 */
public class RequestsResponse {

    private String status;
    private String message;
    private ArrayList<RideRequest> requests;

    public RequestsResponse(String status, String message,  ArrayList<RideRequest> requests) {
        this.status = status;
        this.message = message;
        this.requests = requests;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<RideRequest> getRequests() {
        return this.requests;
    }
}
