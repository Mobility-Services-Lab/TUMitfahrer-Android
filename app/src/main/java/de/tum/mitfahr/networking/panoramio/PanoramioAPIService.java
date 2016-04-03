package de.tum.mitfahr.networking.panoramio;

import de.tum.mitfahr.networking.models.response.StatusResponse;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by abhijith on 09/10/14.
 */
public interface PanoramioAPIService {

    @GET("/get_panoramas.php")
    public PanoramioResponse getPhotos(
            @Query("order") String order,
            @Query("set") String set,
            @Query("from") int from,
            @Query("to") int to,
            @Query("minx") double minx,
            @Query("miny") double miny,
            @Query("maxx") double maxx,
            @Query("maxy") double maxy,
            @Query("size") String medium,
            @Query("mapfilter") boolean mapfilter
    );

}
