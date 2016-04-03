package de.tum.mitfahr.networking.models.response;

import de.tum.mitfahr.networking.models.User;

/**
 * Created by abhijith on 16/05/14.
 */
public class RegisterResponse {
    private String status;
    private User user;
    private String[] errors;

//depending on whether the registration process was successful or not, either the user object is null or the errors
    public RegisterResponse(String status, User user, String[] errors) {
        this.status = status;
        this.user = user;
        this.errors = errors;
    }
    public String getStatus() {
        return status;
    }

    public User getUser(){

        return user;
    }

    public String[] getErrors() {
        return  errors;
    }
}
