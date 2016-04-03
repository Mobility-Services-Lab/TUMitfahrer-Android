package de.tum.mitfahr.networking.models.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.tum.mitfahr.networking.models.Conversation;
import de.tum.mitfahr.networking.models.Rating;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.RideRequest;
import de.tum.mitfahr.networking.models.User;

/**
 * Created by amr on 31/05/14.
 */
public class SearchResponse implements Serializable {

    private int id;
    private String destination;
    private double price;
    private String car;
    private User passengers[];
    private Conversation conversations[];
    private Rating ratings[];
    private RideRequest requests[];
    private boolean is_ride_request;
    private User ride_owner;
    private String departure_place;
    private int ride_type;
    private String departure_time;
    private String created_at;
    private String updated_at;
    private int free_seats;
    private String meeting_point;
    private double departure_latitude;
    private double departure_longitude;
    private double destination_latitude;
    private double destination_longitude;
    private int user_id;
    private double realtime_km;
    private String realtime_departure_time;
    private double regular_ride_id;
    private double is_finished;

    public SearchResponse(int id, double is_finished, String destination, double price, String car, User[] passengers, Rating[] ratings, RideRequest[] requests, Conversation[] conversations, boolean is_ride_request, String departure_place, User ride_owner, int ride_type, String departure_time, String created_at, String updated_at, int free_seats, String meeting_point, double departure_latitude, double departure_longitude, double destination_latitude, double destination_longitude, int user_id, double realtime_km, String realtime_departure_time, double regular_ride_id) {
        this.id = id;
        this.is_finished = is_finished;
        this.destination = destination;
        this.price = price;
        this.car = car;
        this.passengers = passengers;
        this.ratings = ratings;
        this.requests = requests;
        this.conversations = conversations;
        this.is_ride_request = is_ride_request;
        this.departure_place = departure_place;
        this.ride_owner = ride_owner;
        this.ride_type = ride_type;
        this.departure_time = departure_time;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.free_seats = free_seats;
        this.meeting_point = meeting_point;
        this.departure_latitude = departure_latitude;
        this.departure_longitude = departure_longitude;
        this.destination_latitude = destination_latitude;
        this.destination_longitude = destination_longitude;
        this.user_id = user_id;
        this.realtime_km = realtime_km;
        this.realtime_departure_time = realtime_departure_time;
        this.regular_ride_id = regular_ride_id;
    }


    public int getId() {
        return id;
    }

    public String getDestination() {
        return destination;
    }

    public double getPrice() {
        return price;
    }

    public String getCar() {
        return car;
    }

    public User[] getPassengers() {
        return passengers;
    }

    public Conversation[] getConversations() {
        return conversations;
    }

    public Rating[] getRatings() {
        return ratings;
    }

    public RideRequest[] getRequests() {
        return requests;
    }

    public boolean is_ride_request() {
        return is_ride_request;
    }

    public User getRide_owner() {
        return ride_owner;
    }

    public String getDeparture_place() {
        return departure_place;
    }

    public int getRide_type() {
        return ride_type;
    }

    public String getDeparture_time() {
        return departure_time;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public int getFree_seats() {
        return free_seats;
    }

    public String getMeeting_point() {
        return meeting_point;
    }

    public double getDeparture_latitude() {
        return departure_latitude;
    }

    public double getDeparture_longitude() {
        return departure_longitude;
    }

    public double getDestination_latitude() {
        return destination_latitude;
    }

    public double getDestination_longitude() {
        return destination_longitude;
    }

    public int isUser_id() {
        return user_id;
    }

    public double getRealtime_km() {
        return realtime_km;
    }

    public String getRealtime_departure_time() {
        return realtime_departure_time;
    }

    public double getRegular_ride_id() {
        return regular_ride_id;
    }

    public double getIs_finished() {
        return is_finished;
    }

    public List<Ride> getRides() {
        return null;
    }

    public void setDestination_longitude(double destination_longitude) {
        this.destination_longitude = destination_longitude;
    }

    public void setDestination_latitude(double destination_latitude) {
        this.destination_latitude = destination_latitude;
    }
}
