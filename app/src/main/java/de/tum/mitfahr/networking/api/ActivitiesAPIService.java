package de.tum.mitfahr.networking.api;

import de.tum.mitfahr.networking.models.response.ActivitiesResponse;
import de.tum.mitfahr.networking.models.response.BadgesResponse;
import de.tum.mitfahr.networking.models.response.StatusResponse;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;

/**
 * Created by amr on 02/07/14.
 */
public interface ActivitiesAPIService {

    @GET("/activities")
    public void getActivities(
            @Header("Authorization") String apiKey,
            Callback<ActivitiesResponse> callback
    );


    @GET("/activities/badges?campus_updated_at={campusUpdatedAt}&activity_updated_at={activityUpdatedAt}&timeline_updated_at={timelineUpdatedAt}&my_rides_updated_at={myRidesUpdatedAt}&user_id={userId}")
    public void getBadges(
            @Header("Authorization") String apiKey,
            @Path("campusUpdatedAt") String campusUpdatedAt,
            @Path("activityUpdatedAt") String activityUpdatedAt,
            @Path("timelineUpdatedAt") String timelineUpdatedAt,
            @Path("myRidesUpdatedAt") String myRidesUpdatedAt,
            @Path("userId") int userId,
            Callback<BadgesResponse> callback
    );
}
