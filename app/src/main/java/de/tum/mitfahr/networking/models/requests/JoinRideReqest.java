package de.tum.mitfahr.networking.models.requests;

/**
 * Created by abhijith on 01/11/14.
 */
public class JoinRideReqest {

    int passengerId;

    public JoinRideReqest(int passengerId) {
        this.passengerId = passengerId;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }
}
