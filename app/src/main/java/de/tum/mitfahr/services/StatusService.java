package de.tum.mitfahr.services;

import android.app.Activity;
import android.content.Context;

import com.squareup.otto.Subscribe;

import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.GetActivitiesEvent;
import de.tum.mitfahr.events.GetStatusEvent;
import de.tum.mitfahr.networking.clients.StatusRESTClient;
import de.tum.mitfahr.networking.models.response.ActivitiesResponse;
import de.tum.mitfahr.networking.models.response.StatusResponse;

/**
 * Created by kiran on 1/8/2016.
 */
public class StatusService extends AbstractService {

    private StatusRESTClient statusRESTClient;
    public StatusService(final Context context,final Activity activity) {
        super(context);
        String baseBackendURL = TUMitfahrApplication.getApplication(context).getBaseURLBackend();
        statusRESTClient = new StatusRESTClient(baseBackendURL, context, activity);
        getStatus();
    }

    public void getStatus(){
        statusRESTClient.getStatus();
    }

}
