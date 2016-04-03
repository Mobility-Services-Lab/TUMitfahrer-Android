package de.tum.mitfahr.networking.models.requests;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import de.tum.mitfahr.TUMitfahrApplication;

/**
 * Created by abhijith on 08/10/14.
 */
public class CreateRideRequest {

    String departurePlace;
    String destination;
    String meetingPoint;
    int freeSeats;
    String departureTime;
    int rideType;
    //String isDriving;
    double departureLongitude;
    double departureLatitude;
    double destinationLongitude;
    double destinationLatitude;
    List<String> repeatDates;

    int userId;
    String car;
    boolean driver;
    boolean isRideRequest;

    public CreateRideRequest(int userId, String departure, String destination, String meetingPoint,
                             double departureLatitude, double departureLongitude,
                             double destinationLatitude, double destinationLongitude,
                             int freeSeats, String dateTime, int rideType, boolean isDriving,
                             String car, List<String> repeatDates) {
        this.userId = userId;
        this.car = car;
        this.driver = isDriving;
        this.departurePlace = departure;
        this.destination = destination;
        this.meetingPoint = meetingPoint;
        this.freeSeats = freeSeats;
        this.departureTime = dateTime;
        this.rideType = rideType;
        this.isRideRequest = (isDriving)? false : true;
        this.departureLatitude = departureLatitude;
        this.departureLongitude = departureLongitude;
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
        if (isDriving) {
            this.repeatDates = null;
        } else {
            this.repeatDates = repeatDates;
        }
    }
}
