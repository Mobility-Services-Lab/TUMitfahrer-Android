package de.tum.mitfahr.networking.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by amr on 07.07.14.
 */
public class Conversation implements Serializable{

    private int id;
    private int userId;
    private int otherUserId;
    //private Ride ride;
    private ArrayList<Message> messages;

    public Conversation(int id, int userId, int otherUserId, ArrayList<Message> messages) {
        this.id = id;
        this.userId = userId;
        this.otherUserId = otherUserId;
        //this.ride = ride;
        this.messages = messages;
    }
    public Conversation() {
    }
}
