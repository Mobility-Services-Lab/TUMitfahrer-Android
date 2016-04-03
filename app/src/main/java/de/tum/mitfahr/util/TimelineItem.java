package de.tum.mitfahr.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.tum.mitfahr.networking.models.Ride;

/**
 * Created by abhijith on 01/09/14.
 */
public class TimelineItem implements Comparable<TimelineItem> {

    public enum TimelineItemType {
        RIDE_REQUEST,
        RIDE_CREATED,
        RIDE_SEARCHED,
        RIDE_JOIN_REQUEST
    }

    TimelineItemType type;
    String departure;
    String destination;
    double lat = 0.0;
    double lng = 0.0;
    int id;
    Date time;
    Ride ride = null;

    public TimelineItem(Ride ride, TimelineItemType type) {
        this.ride = ride;
        this.type = type;
        this.departure = ride.getDeparturePlace();
        this.destination = ride.getDestination();
        this.id = ride.getId();
        //String updatedAt = ride.getUpdatedAt();
        //Log.e("TimelineItem", type+"  updatedAt: "+updatedAt);
        ///created at selected since the join request is assumed to be before the actual offer if updatedAt is used
        String createdAt = ride.getCreatedAt();
        Log.e("TimelineItem", type+"  updatedAt: "+createdAt);
        try {
            //this.time = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(updatedAt);
            this.time = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(createdAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public TimelineItem(TimelineItemType type, int id, String departure, String destination, String time) {
        this.type = type;
        this.ride = null;
        this.id = id;
        this.departure = departure;
        this.destination = destination;
        try {
            this.time = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public TimelineItemType getType() {
        return type;
    }

    public void setType(TimelineItemType type) {
        this.type = type;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Ride getRide() {
        return this.ride;
    }

    @Override
    public int compareTo(TimelineItem another) {
        return another.getTime().compareTo(this.time);
    }
}
