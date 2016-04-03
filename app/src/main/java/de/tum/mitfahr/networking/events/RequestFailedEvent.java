package de.tum.mitfahr.networking.events;

import de.tum.mitfahr.events.AbstractEvent;
import retrofit.RetrofitError;

/**
 * Created by abhijith on 16/05/14.
 */
public class RequestFailedEvent extends AbstractEvent {

    public enum Type
    {
        CONNECTION_ERROR,
        ERROR_404,
        ERROR_401,
        ERROR_400
    }

    private RetrofitError error;

    public RequestFailedEvent(Type type, RetrofitError error) {
        super(type);
        this.error = error;
    }

    public RetrofitError getError() { return error; }

}
