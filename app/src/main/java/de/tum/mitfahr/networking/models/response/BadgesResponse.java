package de.tum.mitfahr.networking.models.response;

import de.tum.mitfahr.networking.models.BadgeCounter;

/**
 * Created by amr on 02/07/14.
 */
public class BadgesResponse {

    private String status;
    private String message;
    private BadgeCounter badgeCounter;

    private BadgesResponse(String status, String message, BadgeCounter badgeCounter) {
        this.badgeCounter = badgeCounter;
        this.status = status;
        this.message = message;
    }

    public BadgeCounter getBadgeCounter() { return this.badgeCounter; }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
