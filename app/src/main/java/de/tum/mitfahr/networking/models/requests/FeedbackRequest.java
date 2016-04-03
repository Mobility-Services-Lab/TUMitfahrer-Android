package de.tum.mitfahr.networking.models.requests;

/**
 * Created by amr on 07.07.14.
 */
public class FeedbackRequest {

    private String title;
    private String content;

   public FeedbackRequest(String title, String content) {

        this.title = title;
        this.content = content;
    }
}
