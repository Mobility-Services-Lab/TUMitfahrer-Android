package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.RideResponse;

/**
 * Created by amr on 23/06/14.
 */
public class UpdateRideEvent extends AbstractEvent {
    public enum Type
    {
        RIDE_UPDATED,
        UPDATE_FAILED,
        RESULT
    }

    private RideResponse mRideResponse;

    public UpdateRideEvent(Type type, RideResponse mRideResponse) {
        super(type);
        this.mRideResponse = mRideResponse;
    }

    public RideResponse getResponse() {
        return this.mRideResponse;
    }
}
