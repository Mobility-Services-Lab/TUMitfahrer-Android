package de.tum.mitfahr.networking.models.requests;


/**
 * Created by amr on 31/05/14.
 */
public class SearchRequest {
    private String departurePlace;
    private String destination;
    private String departureTime;
    private double departurePlaceThreshold;
    private double destinationThreshold;
    private String rideType;
    private Float departure_latitude;
    private Float departure_longitude;
    private Float destination_latitude;
    private Float destination_longitude;

    public SearchRequest(String from, double departureThreshold, String to, double destinationThreshold,
                         String dateTime, Integer rideType, Float departure_latitude, Float departure_longitude, Float destination_latitude, Float destination_longitude) {
        this.departurePlace = from;
        this.destination = to;
        this.departureTime = dateTime;
        this.departurePlaceThreshold = departureThreshold;
        this.destinationThreshold = destinationThreshold;
        if (rideType == null) {
            this.rideType = null;
        } else {
            this.rideType = Integer.toString(rideType);
        }
        this.departure_latitude = departure_latitude;
        this.departure_longitude = departure_longitude;
        this.destination_latitude = destination_latitude;
        this.destination_longitude = destination_longitude;
    }
}
