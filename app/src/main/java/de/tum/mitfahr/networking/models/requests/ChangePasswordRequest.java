package de.tum.mitfahr.networking.models.requests;

/**
 * Created by Duygu on 21.6.2015.
 */
public class ChangePasswordRequest {
    String password;
    public ChangePasswordRequest(String password) {
        this.password = password;
    }
}
