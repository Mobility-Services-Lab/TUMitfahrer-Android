package de.tum.mitfahr.events;

import java.util.List;

import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.response.SearchResponse;

/**
 * Created by amr on 31/05/14.
 */
public class SearchEvent extends AbstractEvent {

    public enum Type
    {
        SEARCH_SUCCESSFUL,
        SEARCH_FAILED,
        RESULT
    }

    private List<Ride> mSearchResponse;

    public SearchEvent(Type type, List<Ride> searchResponse) {
        super(type);
        this.mSearchResponse = searchResponse;
    }

    public List<Ride> getResponse() {
        return this.mSearchResponse;
    }
}
