package de.tum.mitfahr.networking.api;

import de.tum.mitfahr.networking.models.requests.ChangePasswordRequest;
import de.tum.mitfahr.networking.models.requests.RegisterRequest;
import de.tum.mitfahr.networking.models.requests.UpdateUserRequest;
import de.tum.mitfahr.networking.models.response.ChangePasswordResponse;
import de.tum.mitfahr.networking.models.response.ForgotPasswordResponse;
import de.tum.mitfahr.networking.models.response.GetUserResponse;
import de.tum.mitfahr.networking.models.response.RegisterResponse;
import de.tum.mitfahr.networking.models.response.StatusResponse;
import de.tum.mitfahr.networking.models.response.UpdateUserResponse;
import de.tum.mitfahr.networking.models.response.DepartmentResponse;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by abhijith on 09/05/14.
 */

public interface UserAPIService {

    @POST("/users")
    public void registerUser(
            @Body RegisterRequest user,
            Callback<RegisterResponse> callback
    );


    @GET("/users/{userId}")
    public void getSomeUser(
            @Header("Authorization") String apiKey,
            @Path("userId") int userId,
            Callback<GetUserResponse> callback
    );

    @GET("/users/{userId}")
    public GetUserResponse getUserSynchronous(
            @Header("Authorization") String apiKey,
            @Path("userId") int userId
    );

    @PUT("/users/{id}")
    public void updateUser(
            @Header("Authorization") String auth,
            @Path("id") int userId,
            @Body UpdateUserRequest user,
            Callback<UpdateUserResponse> callback
    );

    @GET("/users/departments")
    public void getDepartments(
        Callback<DepartmentResponse> callback
    );

    @POST("/password/forgot")
    public void forgotPassword(
            @Query("email") String email,
            Callback<ForgotPasswordResponse> callback
    );

    @PUT("/password")
    public void changePassword(
            @Header("Authorization") String auth,
            @Body ChangePasswordRequest request,
            Callback<ChangePasswordResponse> callback
    );

}
