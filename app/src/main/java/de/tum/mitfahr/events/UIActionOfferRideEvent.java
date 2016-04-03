package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.Ride;

/**
 * Created by abhijith on 16/11/14.
 */
public class UIActionOfferRideEvent {

    Ride mRide;

    public UIActionOfferRideEvent(Ride ride) {
        mRide = ride;
    }

    public Ride getRide() {
        return mRide;
    }
}
