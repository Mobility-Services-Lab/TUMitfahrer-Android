package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.RidesResponse;

/**
 * Created by amr on 28/06/14.
 */
public class GetRidesDateEvent extends AbstractEvent{
    public enum Type
    {
        GET_SUCCESSFUL,
        GET_FAILED,
        RESULT
    }

    private RidesResponse mRidesResponse;

    public GetRidesDateEvent(Type type, RidesResponse ridesResponse) {
        super(type);
        this.mRidesResponse = ridesResponse;
    }

    public RidesResponse getResponse() {
        return this.mRidesResponse;
    }
}
