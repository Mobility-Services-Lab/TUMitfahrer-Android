package de.tum.mitfahr.networking.models;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by amr on 18/05/14.
 */
public class Ride implements Serializable {

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

    private double latitude;
    private String rideImageUrl;

    public double getIsFinished() {
        return is_finished;
    }

    public void setIsFinished(double is_finished) {
        this.is_finished = is_finished;
    }

    public double getRegularRideId() {
        return regular_ride_id;
    }

    public void setRegularRideId(double regular_ride_id) {
        this.regular_ride_id = regular_ride_id;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public double getDepartureLatitude() {
        return departure_latitude;
    }

    public void setDepartureLatitude(double departure_latitude) {
        this.departure_latitude = departure_latitude;
    }

    public double getDepartureLongitude() {
        return departure_longitude;
    }

    public void setDepartureLongitude(double departure_longitude) {
        this.departure_longitude = departure_longitude;
    }

    public double getDestinationLatitude() {
        return destination_latitude;
    }

    public void setDestinationLatitude(double destination_latitude) {
        this.destination_latitude = destination_latitude;
    }

    public double getDestinationLongitude() {
        return destination_longitude;
    }

    public void setDestinationLongitude(double destination_longitude) {
        this.destination_longitude = destination_longitude;
    }
    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public User[] getPassengers() {
        return passengers;
    }

    public void setPassengers(User[] passengers) {
        this.passengers = passengers;
    }

    public RideRequest[] getRequests() {
        return requests;
    }

    public void setRequests(RideRequest[] requests) {
        this.requests = requests;
    }

    public Conversation[] getConversations() {
        return conversations;
    }

    public void setConversations(Conversation[] conversations) {
        this.conversations = conversations;
    }
    public void setRatings(Rating[] ratings) {
        this.ratings = ratings;
    }
    public Rating[] getRatings() {
        return ratings;
    }


    public Ride(int id,
                String departure_place,
                String destination,
                String meetingPoint,
                int freeSeats,
                String departureTime,
                double price,
                String realtimeDepartureTime,
                double realtimeKm,
                User rideOwner,
                int rideType,
                String created_at,
                String updated_at) {
        this.id = id;
        this.departure_place = departure_place;
        this.destination = destination;
        this.meeting_point = meetingPoint;
        this.free_seats = freeSeats;
        this.departure_time = departureTime;
        this.price = price;
        this.realtime_departure_time = realtimeDepartureTime;
        this.realtime_km = realtimeKm;
        this.ride_owner = rideOwner;
        this.ride_type = rideType;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.latitude = 0.0;
        this.departure_latitude=0.0;
        this.departure_longitude=0.0;
        this.destination_latitude=0.0;
        this.destination_longitude=0.0;
        // this.longitude = 0.0;
    }

    public Ride(ActivitiesRideSearch searchedRide) {
        this.created_at = searchedRide.getCreatedAt();
        this.departure_place = searchedRide.getDeparturePlace();
        this.departure_time = searchedRide.getDepartureTime();
        this.destination = searchedRide.getDestination();
        this.ride_type = searchedRide.getRideType();
        this.updated_at = searchedRide.getUpdatedAt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getMeetingPoint() {
        return meeting_point;
    }

    public void setMeetingPoint(String meetingPoint) {
        this.meeting_point = meetingPoint;
    }

    public int getFreeSeats() {
        return free_seats;
    }

    public void setFreeSeats(int freeSeats) {
        this.free_seats = freeSeats;
    }

    public String getDepartureTime() {
        return departure_time;
    }

    public void setDepartureTime(String departureTime) {
        this.departure_time = departureTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getRealtimeDepartureTime() {
        return realtime_departure_time;
    }

    public void setRealtimeDepartureTime(String realtimeDepartureTime) {
        this.realtime_departure_time = realtimeDepartureTime;
    }

    public double getRealtimeKm() {
        return realtime_km;
    }

    public void setRealtimeKm(double realtimeKm) {
        this.realtime_km = realtimeKm;
    }

    public User getRideOwner() {
        return ride_owner;
    }

    public void setRideOwner(User driver) {
        this.ride_owner = driver;
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

    public void setUpdatedAt(String updatedAt) {
        this.updated_at = updatedAt;
    }
/*
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }*/

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public boolean isRideRequest() {
        return is_ride_request;
    }

    public void setRideRequest(boolean isRideRequest) {
        this.is_ride_request = isRideRequest;
    }

    public String getRideImageUrl() {
        return rideImageUrl;
    }

    public void setRideImageUrl(String rideImageUrl) {
        this.rideImageUrl = rideImageUrl;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Ride)) {
            return false;
        }
        Ride other = (Ride) obj;
        return this.getId() == other.getId();
    }

    @Override
    public String toString() {
        return "Ride{" +
                "id=" + id +
                ", destination='" + destination + '\'' +
                ", price=" + price +
                ", car='" + car + '\'' +
                ", passengers=" + Arrays.toString(passengers) +
                ", conversations=" + Arrays.toString(conversations) +
                ", ratings=" + Arrays.toString(ratings) +
                ", requests=" + Arrays.toString(requests) +

                ", ride_owner=" + ride_owner +

                ", departure_place='" + departure_place + '\'' +
                ", ride_type=" + ride_type +
                ", departure_time='" + departure_time + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", free_seats=" + free_seats +
                ", meeting_point='" + meeting_point + '\'' +

                ", realtime_km=" + realtime_km +
                ", realtime_departure_time='" + realtime_departure_time + '\'' +


                ", is_ride_request=" + is_ride_request +



                ", latitude=" + latitude +
                ", rideImageUrl='" + rideImageUrl + '\'' +





                //  ", longitude=" + longitude +
                '}';
    }
}
