package de.tum.mitfahr.networking.clients;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.tum.mitfahr.events.SearchEvent;
import de.tum.mitfahr.networking.api.SearchAPIService;
import de.tum.mitfahr.networking.events.RequestFailedEvent;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.requests.SearchRequest;
import de.tum.mitfahr.networking.models.response.SearchResponse;
import de.tum.mitfahr.networking.models.response.StatusResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by amr on 31/05/14.
 */
public class SearchRESTClient extends AbstractRESTClient {

    public SearchRESTClient(String mBaseBackendURL,Context context) {
        super(mBaseBackendURL,context);
    }

    public void search(String userAPIKey, String from, double fromThreshold, String to,
                       double toThreshold, String dateTime, Integer rideType, Float departure_latitude, Float
                       departure_longitude, Float destination_latitude, Float destination_longitude) {
        SearchRequest requestData = new SearchRequest(from, fromThreshold, to, toThreshold,
                dateTime, rideType, departure_latitude, departure_longitude, destination_latitude, destination_longitude);
        SearchAPIService searchAPIService = mRestAdapter.create(SearchAPIService.class);
        searchAPIService.search(userAPIKey, requestData, searchCallback);
    }

    private Callback<List<Ride>> searchCallback = new Callback<List<Ride>>() {

        @Override
        public void success(List<Ride> searchResponse, Response response) {
            Log.i("searchResponse","in Success method event");

            mBus.post(new SearchEvent(SearchEvent.Type.RESULT, searchResponse));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            Log.i("searchResponse","in Failure Method");
            mBus.post(new SearchEvent(SearchEvent.Type.SEARCH_FAILED, null));
            mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,retrofitError));
        }
    };
}
