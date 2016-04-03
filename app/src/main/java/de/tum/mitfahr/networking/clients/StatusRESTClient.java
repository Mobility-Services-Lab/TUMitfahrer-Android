package de.tum.mitfahr.networking.clients;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import de.tum.mitfahr.networking.api.ActivitiesAPIService;
import de.tum.mitfahr.networking.api.StatusAPIService;
import de.tum.mitfahr.networking.models.response.StatusResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by kiran on 1/8/2016.
 */
public class StatusRESTClient extends AbstractRESTClient {

    SharedPreferences mSharedPreferences;
    private StatusAPIService statusAPIService;
    private Context mcontext;
    private Activity mActivity;

    public StatusRESTClient(String mBaseBackendURL, Context context, Activity activity) {
        super(mBaseBackendURL, context);
        this.statusAPIService = mRestAdapter.create(StatusAPIService.class);
        mcontext = context;
        mActivity = activity;
    }

    public void getStatus(){
        statusAPIService.getStatus(statusCallback);
    }

    private Callback<StatusResponse> statusCallback = new Callback<StatusResponse>() {
        @Override
        public void success(StatusResponse statusResponse, Response response) {
           Log.e("ActivitiesRESTClient", "URL Active: " + statusResponse.getStatus() + "");
        }

        @Override
        public void failure(RetrofitError error) {

            Log.e("ActivitiesRESTClient", "URL not active: " + error.getResponse().getReason() + "");

            if(error.getResponse().getStatus() == 410) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
                    alertDialogBuilder.setTitle("Update!");
                    alertDialogBuilder.setMessage("Please update the app").setCancelable(true).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // dialog
                            dialog.cancel();
                            mActivity.finish();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
            }

        }
    };
}
