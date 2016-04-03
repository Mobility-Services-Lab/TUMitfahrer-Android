package de.tum.mitfahr.networking.api;

import de.tum.mitfahr.networking.models.response.StatusResponse;
import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by kiran on 1/8/2016.
 */
public interface StatusAPIService {

    @GET("/")
    public void getStatus(
            Callback<StatusResponse> callback
    );

}
