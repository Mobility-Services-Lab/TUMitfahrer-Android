package de.tum.mitfahr.networking.api;

import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.requests.AcceptRideRequest;
import de.tum.mitfahr.networking.models.requests.JoinRideReqest;
import de.tum.mitfahr.networking.models.requests.OfferRideRequest;
import de.tum.mitfahr.networking.models.requests.RespondRideReqest;
import de.tum.mitfahr.networking.models.response.AcceptRideResponse;
import de.tum.mitfahr.networking.models.response.DeleteRideResponse;
import de.tum.mitfahr.networking.models.response.JoinRequestResponse;
import de.tum.mitfahr.networking.models.response.LeaveRideResponse;
import de.tum.mitfahr.networking.models.response.OfferRideResponse;
import de.tum.mitfahr.networking.models.response.RejectRideResponse;
import de.tum.mitfahr.networking.models.response.RequestsResponse;
import de.tum.mitfahr.networking.models.response.RideResponse;
import de.tum.mitfahr.networking.models.response.RidesResponse;
import de.tum.mitfahr.networking.models.response.StatusResponse;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by amr on 18/05/14.
 */
public interface RidesAPIService {

    @POST("/users/{id}/rides")
    public void offerRide(
            @Header("Authorization") String apiKey,
            @Path("id") int userId,
            @Body OfferRideRequest ride,
            Callback<OfferRideResponse> callback
    );

    @GET("/rides/{id}")
    public void getRide(
            @Header("Authorization") String apiKey,
            @Path("id") int rideId,
            Callback<RideResponse> callback
    );

    @GET("/rides/{id}")
    public RideResponse getRideSynchronous(
            @Header("Authorization") String apiKey,
            @Path("id") int rideId
    );

    @GET("/rides/deleted/{id}")
    public RideResponse getDeletedRideSynchronous(
            @Header("Authorization") String apiKey,
            @Path("id") int rideId
    );

    @PUT("/users/{userId}/rides/{rideId}")
    public void updateRide(
            @Header("Authorization") String apiKey,
            @Path("userId") int userId,
            @Path("rideId") int rideId,
            @Body Ride ride,
            Callback<RideResponse> callback
    );

    @GET("/users/{id}/rides?driver=true")
    public void getMyRidesAsDriver(
            @Header("Authorization") String apiKey,
            @Path("id") int userId,
            @Query("driver") boolean isDriver,
            @Query("past") boolean isPast,
            Callback<RidesResponse> callback
    );

    @GET("/users/{id}/rides")
    public void getMyRidesAsRequester(
            @Header("Authorization") String apiKey,
            @Path("id") int userId,
            @Query("driver") boolean isDriver,
            @Query("past") boolean isPast,
            Callback<RidesResponse> callback
    );

    @GET("/users/{id}/rides?passenger=true&driver=false")
    public void getMyRidesAsPassenger(
            @Header("Authorization") String apiKey,
            @Path("id") int userId,
            Callback<RidesResponse> callback
    );

    @GET("/users/{id}/rides")
    public void getMyRidesPast(
            @Header("Authorization") String apiKey,
            @Path("id") int userId,
            @Query("past") boolean isPast,
            Callback<RidesResponse> callback
    );

    @PUT("/users/{userId}/rides/{rideId}")
    public void removePassenger(
            @Header("Authorization") String apiKey,
            @Path("userId") int userId,
            @Path("rideId") int rideId,
            Callback<RideResponse> callback
    );

    @PUT("/users/{userId}/rides/{rideId}")
    public void addPassenger(
            @Header("Authorization") String apiKey,
            @Path("userId") int userId,
            @Path("rideId") int rideId,
            @Query("added_passenger") int addedPassengerId,
            Callback<RideResponse> callback
    );

    @DELETE("/rides/{rideId}")
    public void deleteRide(
            @Header("Authorization") String apiKey,
            @Path("rideId") int rideId,
            Callback<DeleteRideResponse> callback
    );

    @DELETE("/users/{userId}/rides/{rideId}")
    public void leaveRide(
            @Header("Authorization") String apiKey,
            @Path("userId") int userId,
            @Path("rideId") int rideId,
            Callback<LeaveRideResponse> callback
    );


    @POST("/rides/{rideId}/requests")
    public void joinRequest(
            @Header("Authorization") String apiKey,
            @Path("rideId") int rideId,
            @Body JoinRideReqest request,
            Callback<JoinRequestResponse> callback
    );

    @PUT("/rides/{rideId}/requests")
    public void acceptRideRequest(
            @Header("Authorization") String apiKey,
            @Path("rideId") int rideId,
            @Body AcceptRideRequest request,
            Callback<AcceptRideResponse> callback
    );

    @PUT("/rides/{rideId}/requests")
    public void rejectRideRequest(
            @Header("Authorization") String apiKey,
            @Path("rideId") int rideId,
            @Body AcceptRideRequest request,
            Callback<RejectRideResponse> callback
    );


    @GET("/rides/{rideId}/requests")
    public void getRideRequests(
            @Header("Authorization") String apiKey,
            @Path("rideId") int rideId,
            Callback<RequestsResponse> callback
    );

    @GET("/users/{userId}/requests")
    public void getUserRequests(
            @Header("Authorization") String apiKey,
            @Path("userId") int userId,
            Callback<RequestsResponse> callback
    );

    @DELETE("/rides/{rideId}/requests/{requestId}")
    public void deleteRideRequest(
            @Header("Authorization") String apiKey,
            @Path("rideId") int rideId,
            @Path("requestId") int requestId,
            Callback<RequestsResponse> callback
    );

    @GET("/rides?page={pageNo}")
    public void getPage(
            @Header("Authorization") String apiKey,
            @Path("pageNo") int pageNo,
            Callback<RidesResponse> callback
    );

    @GET("/rides")
    public void getRides(
            @Header("Authorization") String apiKey,
            @Query("from_date") String fromDate,
            @Query("ride_type") int rideType,
            Callback<RidesResponse> callback
    );

    @GET("/rides")
    public void getRides(
            @Header("Authorization") String apiKey,
            @Query("ride_type") int rideType,
            Callback<RidesResponse> callback
    );

    @GET("/rides")
    public void getRidesPaged(
            @Header("Authorization") String apiKey,
            @Query("ride_type") int rideType,
            @Query("page") int page,
            Callback<RidesResponse> callback
    );

}
