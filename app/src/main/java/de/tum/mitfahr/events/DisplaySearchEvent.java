package de.tum.mitfahr.events;

import java.util.List;

import de.tum.mitfahr.networking.models.Ride;

/**
 * Authored by abhijith on 19/06/14.
 */
public class DisplaySearchEvent {
    List<Ride> rides;
    String from;
    String to;

    public List<Ride> getRides() {
        return rides;
    }

    public String getFromLocation() {
        return from;
    }

    public String getToLocation() {
        return to;
    }

    public DisplaySearchEvent(List<Ride> rides, String from, String to) {
        this.rides = rides;
        this.from = from;
        this.to = to;
    }
}
