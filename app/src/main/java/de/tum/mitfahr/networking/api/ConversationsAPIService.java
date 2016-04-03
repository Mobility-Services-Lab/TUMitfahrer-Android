package de.tum.mitfahr.networking.api;

import de.tum.mitfahr.networking.models.response.ConversationResponse;
import de.tum.mitfahr.networking.models.response.ConversationsResponse;
import de.tum.mitfahr.networking.models.response.StatusResponse;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;

/**
 * Created by amr on 07.07.14.
 */
public interface ConversationsAPIService {

    @GET("/rides/{rideId}/conversations")
    public void getConversations(
            @Header("Authorization") String apiKey,
            @Path("rideId") int rideId,
            Callback<ConversationsResponse> callback
    );


    @GET("/rides/{rideId}/conversations/{conversationId}")
    public void getConversation(
            @Header("Authorization") String apiKey,
            @Path("rideId") int rideId,
            @Path("conversationId") int conversationId,
            Callback<ConversationResponse> callback
    );
}
