package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.RidesResponse;

/**
 * Created by Abhijith on 15/06/14.
 */
public class MyRidesAsPassengerEvent extends AbstractEvent {

    public enum Type
    {
        GET_SUCCESSFUL,
        GET_FAILED,
        RESULT
    }

    private RidesResponse mRidesResponse;

    public MyRidesAsPassengerEvent(Type type, RidesResponse ridesResponse) {
        super(type);
        this.mRidesResponse = ridesResponse;
    }

    public RidesResponse getResponse() {
        return this.mRidesResponse;
    }
}
