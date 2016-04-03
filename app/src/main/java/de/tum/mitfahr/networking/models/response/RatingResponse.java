package de.tum.mitfahr.networking.models.response;

import java.util.ArrayList;

import de.tum.mitfahr.networking.models.Rating;

/**
 * Created by amr on 29/06/14.
 */
public class RatingResponse {

    private String status;
    private String message;
    private Rating rating;

    public RatingResponse(String status, String message, Rating rating) {
        this.status = status;
        this.message = message;
        this.rating = rating;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Rating getRating() {
        return rating;
    }
}
