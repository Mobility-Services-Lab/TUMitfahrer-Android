package de.tum.mitfahr.events;

import retrofit.client.Response;

/**
 * Created by amr on 08.07.14.
 */
public class SendFeedbackEvent extends AbstractEvent {

    public enum Type
    {
        SUCCESSFUL,
        FAILED,
        RESULT
    }

    Response mRetrofitResponse;

    public SendFeedbackEvent(Type type, Response response) {
        super(type);
        this.mRetrofitResponse = response;
    }

    public Response getRetrofitResponse() { return  this.mRetrofitResponse; }
}
