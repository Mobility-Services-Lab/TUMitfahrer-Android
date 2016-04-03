package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.BadgesResponse;

/**
 * Created by amr on 02/07/14.
 */
public class GetBadgesEvent extends AbstractEvent {

    public enum Type
    {
        GET_SUCCESSFUL,
        GET_FAILED,
        RESULT
    }

    private BadgesResponse mBadgesResponse;

    public GetBadgesEvent(Type type, BadgesResponse badgesResponse) {
        super(type);
        this.mBadgesResponse = badgesResponse;
    }

    public BadgesResponse getResponse() {
        return this.mBadgesResponse;
    }
}
