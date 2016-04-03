package de.tum.mitfahr.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.tum.mitfahr.R;

/**
 * Created by abhijith on 19/08/14.
 */
public class LocationUtil {

    public static final double EARTH_RADIUS = 6372.8; // In kilometers

    public static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return EARTH_RADIUS * c;
    }

    //TODO: causes lag in error response when not connected to internet, use it in an asyncTask?
    public static LatLng getLocationFromAddress(Geocoder coder, String strAddress) {

        List<Address> address;
        LatLng dummyLatLng = new LatLng(0, 0);

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return dummyLatLng;
            }
            if(address.size()==0) {
                return dummyLatLng;
            }
            Address location = address.get(0);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            return latLng;

        } catch (IOException e) {
            e.printStackTrace();
            //Google server timeout
            return null;
        }
    }
}
