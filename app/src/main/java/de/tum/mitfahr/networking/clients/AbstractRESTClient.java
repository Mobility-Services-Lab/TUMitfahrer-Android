package de.tum.mitfahr.networking.clients;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;

import java.util.concurrent.TimeUnit;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.networking.events.RequestFailedEvent;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;

/**
 * Created by abhijith on 09/05/14.
 */

public abstract class AbstractRESTClient implements ErrorHandler {

    RestAdapter mRestAdapter;
    String mBaseBackendURL;
    Bus mBus;
    SharedPreferences sharedPreferences;
    Context context;

    protected AbstractRESTClient(String mBaseBackendURL,Context context) {
        this.mBaseBackendURL = mBaseBackendURL;
        this.mBus = BusProvider.getInstance();
        this.context = context;
        mBus.register(this);
        configureRestAdapter();
    }

    protected void configureRestAdapter() {

        this.mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(mBaseBackendURL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(this)
                .setClient(new OkClient(getClient()))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Content-Type", "application/json;charset=utf-8");
                        request.addHeader("Accept", "application/json;charset=utf-8");
                        request.addHeader("Accept-Language", "en");
                    }
                })
                .setConverter(getGsonConverter())
                .build();
    }

    public OkHttpClient getClient() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(3, TimeUnit.MINUTES);
        client.setReadTimeout(3, TimeUnit.MINUTES);
        return client;
    }

    protected GsonConverter getGsonConverter() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        return new GsonConverter(gson);
    }

    @Override
    public Throwable handleError(RetrofitError retrofitError) {
        // Do some checking with the response here!
        return retrofitError;
    }

    public void handleErrorResponse(RetrofitError error) {

            if (null == error.getResponse()) {
                mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR, error));
            }
            else if (error.getResponse().getStatus() == 404) {
                mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.ERROR_404, error));
            }
            else if (error.getResponse().getStatus() == 401) {
                mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.ERROR_401, error));
            }
            else if (error.getResponse().getStatus() == 400) {
                mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.ERROR_400, error));
            }
        }
    }

