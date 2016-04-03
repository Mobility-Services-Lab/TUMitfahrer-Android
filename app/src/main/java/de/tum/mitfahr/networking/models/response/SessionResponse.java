package de.tum.mitfahr.networking.models.response;

/**
 * Created by Duygu on 04/08/2015.
 */
public class SessionResponse {

    private String status;

    public SessionResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
