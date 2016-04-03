package de.tum.mitfahr.networking.models.response;

/**
 * Created by abhijith on 03/12/14.
 */
public class ForgotPasswordResponse {

    private String message;
    private String status;

    public ForgotPasswordResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
