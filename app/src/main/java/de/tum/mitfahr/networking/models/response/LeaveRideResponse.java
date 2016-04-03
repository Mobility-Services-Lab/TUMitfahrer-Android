package de.tum.mitfahr.networking.models.response;

/**
 * Created by Duygu on 13/07/2015.
 */
public class LeaveRideResponse {

    private String status;
    private String message;

    public LeaveRideResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
