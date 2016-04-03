package de.tum.mitfahr.networking.api;

import de.tum.mitfahr.networking.models.requests.FeedbackRequest;
import de.tum.mitfahr.networking.models.response.FeedbackResponse;
import de.tum.mitfahr.networking.models.response.StatusResponse;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by amr on 07.07.14.
 */
public interface FeedbackAPIService {

    @POST("/feedback")
    public void sendFeedback(
            @Header("Authorization") String apiKey,
            @Body FeedbackRequest feedback,
            Callback<FeedbackResponse> callback
    );

}
