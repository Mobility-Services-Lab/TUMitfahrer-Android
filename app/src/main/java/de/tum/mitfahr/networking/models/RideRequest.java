package de.tum.mitfahr.networking.models;

import java.io.Serializable;

/**
 * Created by amr on 21/06/14.
 */
public class RideRequest implements Serializable {

    private int id;
    private int passengerId;
    private Ride ride;
    private String createdAt;
    private String updatedAt;

    public RideRequest(int id, int passengerId, Ride ride) {
        this.id = id;
        this.passengerId = passengerId;
        this.ride = ride;
    }

    public RideRequest(int id, int passengerId, Ride ride, String createdAt, String updatedAt) {
        this.id = id;
        this.passengerId = passengerId;
        this.ride = ride;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
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


    @Override
    public String toString() {
        String rideStr = (ride!=null) ? ", " +ride.toString() : "";
        String str = "{RideRequest: " +
                "id=" + id +
                ", passengerId=" + passengerId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                rideStr+
                "}";
        return str;
    }
}
