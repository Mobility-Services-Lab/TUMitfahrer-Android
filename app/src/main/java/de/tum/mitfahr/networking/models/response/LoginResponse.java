package de.tum.mitfahr.networking.models.response;

import com.google.gson.annotations.SerializedName;

import de.tum.mitfahr.networking.models.User;

/**
 * Created by abhijith on 16/05/14.
 */

public class LoginResponse {

    private String status;
    private String message;
    private User user;

    public LoginResponse(String status, String message, User user) {
        this.status = status;
        this.message = message;
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", user=" + user +
                '}';
    }
}
