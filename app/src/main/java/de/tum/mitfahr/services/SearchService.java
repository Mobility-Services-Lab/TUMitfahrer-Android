package de.tum.mitfahr.services;

import android.content.Context;
import android.preference.PreferenceManager;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.SearchEvent;
import de.tum.mitfahr.networking.clients.SearchRESTClient;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.response.SearchResponse;

/**
 * Created by amr on 31/05/14.
 */
public class SearchService extends AbstractService {

    private SearchRESTClient mSearchRESTClient;
    private Bus mBus;

    public SearchService(final Context context) {
        super(context);
        String baseBackendURL = TUMitfahrApplication.getApplication(context).getBaseURLBackend();
        mBus = BusProvider.getInstance();
        mBus.register(this);
        mSearchRESTClient = new SearchRESTClient(baseBackendURL,context);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void search(String from, double fromThreshold, String to, double toThreshold, String dateTime, Integer rideType, Float departure_latitude, Float departure_longitude, Float destination_latitude, Float destination_longitude) {
        String userAPIKey = mSharedPreferences.getString("api_key", null);
        mSearchRESTClient.search(userAPIKey, from, fromThreshold, to, toThreshold, dateTime, rideType, departure_latitude, departure_longitude, destination_latitude, destination_longitude);
    }

    @Subscribe
    public void onSearchResult(SearchEvent result) {
        if (result.getType() == SearchEvent.Type.RESULT) {
            List<Ride> response = result.getResponse();


            if (response == null || response.size() == 0){
                mBus.post(new SearchEvent(SearchEvent.Type.SEARCH_FAILED, response));
            } else {
                mBus.post(new SearchEvent(SearchEvent.Type.SEARCH_SUCCESSFUL, response));
            }
        }
    }
}
