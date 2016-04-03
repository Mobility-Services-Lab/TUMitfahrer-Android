package de.tum.mitfahr.networking.clients;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import de.tum.mitfahr.networking.models.response.StatusResponse;
import de.tum.mitfahr.networking.panoramio.PanoramioAPIService;
import de.tum.mitfahr.networking.panoramio.PanoramioPhoto;
import de.tum.mitfahr.networking.panoramio.PanoramioResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by abhijith on 09/10/14.
 */
public class PanoramioRESTClient extends AbstractRESTClient {

    private PanoramioAPIService panoramioAPIService;

    public PanoramioRESTClient(String mBaseBackendURL, Context context) {
        super(mBaseBackendURL, context);
        panoramioAPIService = mRestAdapter.create(PanoramioAPIService.class);
    }

    public PanoramioPhoto getPhoto(double minx, double miny, double maxx, double maxy) {
      //  panoramioAPIService.getStatus(statusCallback);
        PanoramioResponse response = panoramioAPIService.getPhotos
                ("popularity", "public", 0, 20, minx, miny, maxx, maxy, "medium", true);
        if (response != null) {
            if (response.getCount() > 0) {
                return response.getPhotos()[0];
            } else {
                return null;
            }
        }
        return null;
    }


//    private Callback<StatusResponse> statusCallback = new Callback<StatusResponse>() {
//        @Override
//        public void success(StatusResponse statusResponse, Response response) {
//            Log.e("ActivitiesRESTClient", "URL Active: " + statusResponse.getStatus() + "");
//        }
//
//        @Override
//        public void failure(RetrofitError error) {
//
//            Log.e("ActivitiesRESTClient", "URL not active: "+error.getResponse().getReason()+"");
//
//            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean("oldAPI",true);
//            editor.commit();
//
//        }
//    };

}
