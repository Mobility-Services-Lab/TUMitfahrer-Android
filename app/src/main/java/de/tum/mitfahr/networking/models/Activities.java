package de.tum.mitfahr.networking.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by amr on 02/07/14.
 */
public class Activities implements Serializable {

    private int id;
    private ArrayList<Ride> rides;
    private ArrayList<ActivitiesRideRequest> requests;
    private ArrayList<ActivitiesRideSearch> rideSearches;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Ride> getRides() {
        return rides;
    }

    public void setRides(ArrayList<Ride> rides) {
        this.rides = rides;
    }

    public ArrayList<ActivitiesRideRequest> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<ActivitiesRideRequest> requests) {
        this.requests = requests;
    }

    public ArrayList<ActivitiesRideSearch> getRideSearches() {
        return rideSearches;
    }

    public void setRideSearches(ArrayList<ActivitiesRideSearch> rideSearches) {
        this.rideSearches = rideSearches;
    }

    public Activities(int id, ArrayList<Ride> rides, ArrayList<ActivitiesRideRequest> rideRequests,
                      ArrayList<ActivitiesRideSearch> rideSearches) {
        this.id = id;
        this.rides = rides;
        this.requests = rideRequests;
        this.rideSearches = rideSearches;
    }
}
