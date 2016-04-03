package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.ActivitiesResponse;
import de.tum.mitfahr.networking.models.response.StatusResponse;
import retrofit.client.Response;

/**
 * Created by kiran on 1/8/2016.
 */
public class GetStatusEvent extends AbstractEvent {
    public enum Type
    {
        GET_SUCCESSFUL,
        GET_FAILED,
        RESULT
    }

    private StatusResponse mStatusResponse;
    private Response retrofitResponse;

    protected GetStatusEvent(Type type, StatusResponse statusResponse, Response retrofitResponse) {
        super(type);
        this.mStatusResponse = statusResponse;
        this.retrofitResponse = retrofitResponse;
    }

    public StatusResponse getResponse() {
            return mStatusResponse;
    }

    public Response getRetrofitResponse() {
        return retrofitResponse;
    }


}
