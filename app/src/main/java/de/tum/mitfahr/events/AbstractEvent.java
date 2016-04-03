package de.tum.mitfahr.events;

/**
 * Created by amr on 25/05/14.
 */
public abstract class AbstractEvent {
    private Enum mType;

    protected AbstractEvent(Enum type)
    {
        this.mType = type;
    }

    public Enum getType()
    {
        return this.mType;
    }
}
