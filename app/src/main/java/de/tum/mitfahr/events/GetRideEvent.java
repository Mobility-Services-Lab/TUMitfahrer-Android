package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.RideResponse;

/**
 * Created by amr on 23/06/14.
 */
public class GetRideEvent extends AbstractEvent {

    public enum Type
    {
        GET_SUCCESSFUL,
        GET_FAILED,
        RESULT
    }

    private RideResponse mRideResponse;

    public GetRideEvent(Type type, RideResponse mRideResponse) {
        super(type);
        this.mRideResponse = mRideResponse;
    }

    public RideResponse getResponse() {
        return this.mRideResponse;
    }
}
