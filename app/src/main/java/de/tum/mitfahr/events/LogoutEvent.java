package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.LogoutResponse;

/**
 * Created by Duygu on 18.6.2015.
 */
public class LogoutEvent  extends AbstractEvent  {

    private LogoutResponse response;

    public enum Type
    {
        LOGOUT_FAILED,
        LOGOUT_SUCCESSFUL,
        LOGOUT_RESULT
    }

    protected LogoutEvent(Enum type) {
        super(type);
    }

    public LogoutEvent(Type type, LogoutResponse response) {

        super(type);
        this.response = response;
    }

    public LogoutResponse getResponse() {
        return response;
    }
}
