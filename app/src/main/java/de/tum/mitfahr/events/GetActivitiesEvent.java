package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.ActivitiesResponse;
import retrofit.client.Response;

/**
 * Created by amr on 02/07/14.
 */
public class GetActivitiesEvent extends AbstractEvent {
    public enum Type
    {
        GET_SUCCESSFUL,
        GET_FAILED,
        RESULT
    }

    private ActivitiesResponse mActivitiesResponse;
    private Response retrofitResponse;

    public GetActivitiesEvent(Type type, ActivitiesResponse activitiesResponse, Response retrofitResponse) {
        super(type);
        this.mActivitiesResponse = activitiesResponse;
        this.retrofitResponse = retrofitResponse;
    }

    public ActivitiesResponse getResponse() {
        return this.mActivitiesResponse;
    }

    public Response getRetrofitResponse() { return this.retrofitResponse; }
}
