package de.tum.mitfahr.networking.api;

import java.io.InputStream;

import de.tum.mitfahr.networking.models.response.UploadAvatarResponse;
import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

/**
 * Created by Duygu on 18.6.2015.
 */
public interface AvatarAPIService {

    @GET("/users/{userId}/avatar/{avatarId}")
    public void getAvatarByAvatarId(
            @Path("userId") int userId,
            @Path("avatarId") int avatarId,
            Callback<InputStream> callback
    );

    @GET("/users/{userId}/avatar")
    public void getAvatar(
            @Path("userId") int userId,
            Callback<InputStream> callback
    );

    @DELETE("/users/{userId}/avatar")
    public void deleteAvatar(
            @Header("Authorization") String apiKey,
            @Path("userId") int userId,
            Callback<UploadAvatarResponse> callback //TODO: change
    );

    @Multipart
    @POST("/users/{userId}/avatar")
    public void uploadAvatar(
            @Header("Authorization") String apiKey,
            @Path("userId") int userId,
            @Part(value = "uploadImage") TypedFile file,
            Callback<UploadAvatarResponse> callback
    );
}
