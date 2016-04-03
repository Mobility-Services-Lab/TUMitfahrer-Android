package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.response.OfferRideResponse;

/**
 * Created by amr on 25/05/14.
 */
public class OfferRideEvent extends AbstractEvent {

    public enum Type
    {
        RIDE_ADDED,
        OFFER_RIDE_FAILED,
        RESULT
    }

    OfferRideResponse mResponse;
    private Ride ride;

    public OfferRideEvent(Type type)
    {
        super(type);
    }

    // For when result is available
    public OfferRideEvent(Type type, OfferRideResponse response) {

        super(type);
        this.mResponse = response;
    }

    // On ride added event
    public OfferRideEvent(Type type, Ride ride) {

        super(type);
        this.ride = ride;

    }

    public OfferRideResponse getResponse() {
        return mResponse;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

}
