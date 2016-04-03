package de.tum.mitfahr.networking.models;

import java.io.Serializable;

/**
 * Created by amr on 02/07/14.
 */
public class ActivitiesRideRequest  implements Serializable {

    private int id;
    private int passengerId;
    private int rideId;
    private String createdAt;
    private String updatedAt;

    public ActivitiesRideRequest(int id, int passengerId, int rideId) {
        this.id = id;
        this.passengerId = passengerId;
        this.rideId = rideId;
    }

    public ActivitiesRideRequest(int id, int passengerId, int rideId, String createdAt, String updatedAt) {
        this.id = id;
        this.passengerId = passengerId;
        this.rideId = rideId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getRideId() {
        return rideId;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
