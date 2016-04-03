package de.tum.mitfahr.networking.models;

/**
 * Created by amr on 02/07/14.
 */
public class BadgeCounter {

    private int id;
    private String createdAt;
    private int timelineBadge;
    private String timelineUpdatedAt;
    private int campusBadge;
    private String campusUpdatedAt;
    private int activityBadge;
    private String activityUpdatedAt;
    private int myRidesBadge;
    private String myRidesUpdatedAt;

    public BadgeCounter(int id, String createdAt, int timelineBadge, String timelineUpdatedAt,
                          int campusBadge, String campusUpdatedAt, int activityBadge, String activityUpdatedAt, int myRidesBadge, String myRidesUpdatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.timelineBadge = timelineBadge;
        this.timelineUpdatedAt = timelineUpdatedAt;
        this.campusBadge = campusBadge;
        this.campusUpdatedAt = campusUpdatedAt;
        this.activityBadge = activityBadge;
        this.activityUpdatedAt = activityUpdatedAt;
        this.myRidesBadge = myRidesBadge;
        this.myRidesUpdatedAt = myRidesUpdatedAt;
    }
}
