package de.tum.mitfahr.networking.panoramio;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.otto.Bus;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.networking.clients.PanoramioRESTClient;

/**
 * Created by abhijith on 09/10/14.
 */
public class PanoramioService {

    private SharedPreferences mSharedPreferences;
    private PanoramioRESTClient mPanoramioRESTClient;
    private Bus mBus;

    public PanoramioService(Context context) {
        String baseURL = TUMitfahrApplication.getApplication(context).getPanoramioURLBackend();
        mBus = BusProvider.getInstance();
        mBus.register(this);
        mPanoramioRESTClient = new PanoramioRESTClient(baseURL,context);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public PanoramioPhoto getPhoto(double x, double y) {
        double dist1 = 0.028;
        double dist2 = 1;
        PanoramioPhoto photo = mPanoramioRESTClient.getPhoto(x-dist1, y-dist1, x+dist1, y+dist1);
        if(photo==null) photo = mPanoramioRESTClient.getPhoto(x-dist2, y-dist2, x+dist2, y+dist2);
        return photo;
    }

}
