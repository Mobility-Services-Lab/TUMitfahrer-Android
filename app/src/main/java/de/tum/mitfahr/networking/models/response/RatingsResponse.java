package de.tum.mitfahr.networking.models.response;

import java.util.ArrayList;

import de.tum.mitfahr.networking.models.Rating;

/**
 * Created by amr on 29/06/14.
 */
public class RatingsResponse {

    private String status;
    private String message;
    private ArrayList<Rating> ratings;

    public RatingsResponse(String status, String message, ArrayList<Rating> ratings) {
        this.status = status;
        this.message = message;
        this.ratings = ratings;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Rating> getRatings() {
        return ratings;
    }
}
