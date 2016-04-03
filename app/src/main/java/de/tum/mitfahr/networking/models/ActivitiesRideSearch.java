package de.tum.mitfahr.networking.models;

import java.io.Serializable;

/**
 * Created by amr on 02/07/14.
 */
public class ActivitiesRideSearch  implements Serializable {

    private int id;
    private int userId;
    private String departure_place;
    private String destination;
    private String departure_time;
    private int ride_type;
    private String created_at;
    private String updated_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDeparturePlace() {
        return departure_place;
    }

    public void setDeparturePlace(String departurePlace) {
        this.departure_place = departurePlace;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDepartureTime() {
        return departure_time;
    }

    public void setDepartureTime(String departureTime) {
        this.departure_time = departureTime;
    }

    public int getRideType() {
        return ride_type;
    }

    public void setRideType(int rideType) {
        this.ride_type = rideType;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String createdAt) {
        this.created_at = createdAt;
    }

    public String getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(String updated_at) {
        this.updated_at = updated_at;
    }

    public ActivitiesRideSearch(int id, int userId, String departure_place, String destination,
                                String departureTime, int rideType, String createdAt, String updated_at) {

        this.created_at = createdAt;
        this.departure_place = departure_place;
        this.departure_time = departureTime;
        this.destination = destination;
        this.id = id;
        this.ride_type = rideType;
        this.userId = userId;
        this.updated_at = updated_at;
    }
}
