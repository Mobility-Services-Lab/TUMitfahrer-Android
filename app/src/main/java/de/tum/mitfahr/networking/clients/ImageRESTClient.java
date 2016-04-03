package de.tum.mitfahr.networking.clients;

import com.squareup.otto.Bus;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import de.tum.mitfahr.BusProvider;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Created by Duygu on 21.6.2015.
 */
public class ImageRESTClient implements ErrorHandler {

    RestAdapter mRestAdapter;
    String mBaseBackendURL;
    Bus mBus;

    protected ImageRESTClient(String mBaseBackendURL) {
        this.mBaseBackendURL = mBaseBackendURL;
        this.mBus = BusProvider.getInstance();
        mBus.register(this);
        configureRestAdapter();
    }

    protected void configureRestAdapter() {

        this.mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(mBaseBackendURL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(this)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Content-Type", "multipart/form-data");
                        request.addHeader("Accept", "*/*");
                        //request.addHeader("Accept-Language", "en");
                    }
                })
                //.setConverter(new ImageConverter())
                .build();
    }

    @Override
    public Throwable handleError(RetrofitError retrofitError) {
        return retrofitError;
    }

    private class ImageConverter implements Converter {
        @Override
        public Object fromBody(TypedInput body, Type type) throws ConversionException {
            InputStream inputStream = null;
            try {
                inputStream = body.in();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return inputStream;
        }

        @Override
        public TypedOutput toBody(Object object) {
            return null;
        }
    }
}
