package de.tum.mitfahr.networking.api;

import java.util.List;

import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.requests.SearchRequest;
import de.tum.mitfahr.networking.models.response.SearchResponse;
import de.tum.mitfahr.networking.models.response.StatusResponse;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by amr on 31/05/14.
 */
public interface SearchAPIService {

    @POST("/search")
    public void search(
            @Header("Authorization") String apiKey,
            @Body SearchRequest searchRequest,
            Callback<List<Ride>> callback
    );

}
