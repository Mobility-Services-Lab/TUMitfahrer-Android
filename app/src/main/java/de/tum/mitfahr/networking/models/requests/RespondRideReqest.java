package de.tum.mitfahr.networking.models.requests;

/**
 * Created by abhijith on 01/11/14.
 */
public class RespondRideReqest {

    int passengerId;
    boolean confirmed;

    public RespondRideReqest(int passengerId, boolean confirmed) {
        this.passengerId = passengerId;
        this.confirmed = confirmed;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}
