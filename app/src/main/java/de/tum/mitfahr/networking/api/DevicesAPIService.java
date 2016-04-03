package de.tum.mitfahr.networking.api;

import de.tum.mitfahr.networking.models.Device;
import de.tum.mitfahr.networking.models.requests.RegisterDeviceRequest;
import de.tum.mitfahr.networking.models.response.GetUserDevicesResponse;
import de.tum.mitfahr.networking.models.response.StatusResponse;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by Duygu on 14/07/2015.
 */
public interface DevicesAPIService {

    @GET("/users/{userId}/devices")
    void getUserDevices(@Header("Authorization") String apiKey,
                        @Path("userId") int userId,
                        Callback<GetUserDevicesResponse> callback);


    @POST("/users/{userId}/devices")
    void registerUserDevice(@Header("Authorization") String apiKey,
                            @Path("userId") int userId,
                            @Body RegisterDeviceRequest request,
                            Callback<Device> callback);

    @DELETE("/users/{userId}/devices/{deviceId}")
    void disableUserDevice(@Header("Authorization") String apiKey,
                            @Path("userId") int userId,
                           @Path("deviceId") int deviceId,
                            Callback<Response> callback);

}
