package de.tum.mitfahr.networking.models.response;

/**
 * Created by Duygu on 18.6.2015.
 */
public class LogoutResponse {

    private String status;
    private String message;

    public LogoutResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }


    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "LogoutResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
