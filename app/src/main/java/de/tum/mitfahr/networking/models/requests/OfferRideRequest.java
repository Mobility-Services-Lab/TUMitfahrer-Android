package de.tum.mitfahr.networking.models.requests;

import java.util.List;

/**
 * Created by amr on 18/05/14.
 */
public class OfferRideRequest {

    CreateRideRequest ride;

    public OfferRideRequest(int userId, String departure, String destination, String meetingPoint,
                            double departureLatitude, double departureLongitude,
                            double destinationLatitude, double destinationLongitude,
                            int freeSeats, String dateTime, int rideType, boolean isDriving, String car,List<String> repeatDates) {
        ride = new CreateRideRequest(userId, departure, destination, meetingPoint,departureLatitude, departureLongitude, destinationLatitude, destinationLongitude, freeSeats, dateTime, rideType, isDriving, car, repeatDates);
    }
}
