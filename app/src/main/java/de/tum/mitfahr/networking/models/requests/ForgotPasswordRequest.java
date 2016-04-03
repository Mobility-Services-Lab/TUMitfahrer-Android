package de.tum.mitfahr.networking.models.requests;

/**
 * Created by abhijith on 03/12/14.
 */
public class ForgotPasswordRequest {

    private final String email;

    public ForgotPasswordRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
