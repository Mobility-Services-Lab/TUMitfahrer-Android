package de.tum.mitfahr;

import com.squareup.otto.Bus;

/**
 * Created by abhijith on 09/05/14.
 */
public class BusProvider {

    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}
