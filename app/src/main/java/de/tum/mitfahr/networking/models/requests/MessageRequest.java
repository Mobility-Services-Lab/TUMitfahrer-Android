package de.tum.mitfahr.networking.models.requests;

/**
 * Created by amr on 07.07.14.
 */
public class MessageRequest {

    private int senderId;
    private int receiverId;
    private String Content;

    public MessageRequest(int senderId, int receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        Content = content;
    }
}
