package de.tum.mitfahr.networking.models.response;

import java.util.ArrayList;

import de.tum.mitfahr.networking.models.Conversation;

/**
 * Created by amr on 07.07.14.
 */
public class ConversationsResponse {

    private String status;
    private String message;
    private ArrayList<Conversation> conversations;

    public ConversationsResponse(String status, String message, ArrayList<Conversation> conversations) {
        this.status = status;
        this.message = message;
        this.conversations = conversations;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Conversation> getConversations() {
        return conversations;
    }
}
