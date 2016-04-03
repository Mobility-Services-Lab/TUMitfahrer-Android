package de.tum.mitfahr.networking.api;

import de.tum.mitfahr.networking.models.response.LoginResponse;
import de.tum.mitfahr.networking.models.response.LogoutResponse;
import de.tum.mitfahr.networking.models.response.SessionResponse;
import de.tum.mitfahr.networking.models.response.StatusResponse;
import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;

/**
 * Created by abhijith on 09/05/14.
 */

public interface SessionAPIService {

    @POST("/sessions")
    void loginUser(@Header("Authorization") String auth, Callback<LoginResponse> callback);


    @DELETE("/sessions")
    void logoutUser(@Header("Authorization") String apiKey, Callback<LogoutResponse> callback);

    @PUT("/sessions")
    public void checkSessionValidity(@Header("Authorization") String apiKey, Callback<SessionResponse> callback);

}
