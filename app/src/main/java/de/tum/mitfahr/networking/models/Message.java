package de.tum.mitfahr.networking.models;

import java.io.Serializable;

/**
 * Created by amr on 07.07.14.
 */
public class Message implements Serializable{

    private int id;
    private String Content;
    private boolean isSeen;
    private int senderId;
    private int receiverId;
    private String createdAt;
    private String updatedAt;

    public Message(int id, String content, boolean isSeen, int senderId, String createdAt,
                   int receiverId, String updatedAt) {
        this.id = id;
        Content = content;
        this.isSeen = isSeen;
        this.senderId = senderId;
        this.createdAt = createdAt;
        this.receiverId = receiverId;
        this.updatedAt = updatedAt;
    }
}
