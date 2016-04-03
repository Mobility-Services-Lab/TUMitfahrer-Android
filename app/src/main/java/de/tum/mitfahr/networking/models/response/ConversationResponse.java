package de.tum.mitfahr.networking.models.response;

import java.util.ArrayList;

import de.tum.mitfahr.networking.models.Conversation;

/**
 * Created by amr on 07.07.14.
 */
public class ConversationResponse {

    private String status;
    private String message;
    private Conversation conversation;

    public ConversationResponse(String status, String message, Conversation conversation) {
        this.status = status;
        this.message = message;
        this.conversation = conversation;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Conversation getConversation() {
        return conversation;
    }
}
