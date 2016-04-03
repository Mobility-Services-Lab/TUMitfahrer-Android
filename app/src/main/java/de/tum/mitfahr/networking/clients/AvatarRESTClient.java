package de.tum.mitfahr.networking.clients;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.tum.mitfahr.events.UploadAvatarEvent;
import de.tum.mitfahr.networking.api.AvatarAPIService;
import de.tum.mitfahr.networking.models.response.UploadAvatarResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Duygu on 18.6.2015.
 */
public class AvatarRESTClient extends ImageRESTClient {

    private static final String TAG = AvatarRESTClient.class.getSimpleName();
    private AvatarAPIService avatarAPIService;

    public AvatarRESTClient(String mBaseBackendURL) {
        super(mBaseBackendURL);
        avatarAPIService = mRestAdapter.create(AvatarAPIService.class);
    }

    public String getAvatarUrl(int userId) {
        return mBaseBackendURL+"/users/"+userId+"/avatar";
    }

    public void uploadAvatar(final String userAPIKey, final int userId, final File payload) {
        //avatarAPIService.uploadAvatar(userAPIKey, userId, payload, uploadAvatarCallback );
        new UploadImageTask(userAPIKey, userId).execute(payload);
    }

    public int uploadFile(File sourceFile, String userAPIKey, int userId) {


        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        String fileName = sourceFile.getName();
        int serverResponseCode = 0;

        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File not exist ");
            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(mBaseBackendURL+"/users/"+userId+"/avatar");

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Authorization", userAPIKey);
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploadImage", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadImage\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);


                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    Log.e(TAG, "File Upload Complete.");

                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                Log.e("Upload file to server", "Exception : "  + e.getMessage(), e);
            }
            return serverResponseCode;

        }
    }

    private Callback<UploadAvatarResponse> uploadAvatarCallback = new Callback<UploadAvatarResponse>() {
        @Override
        public void success(UploadAvatarResponse uploadAvatarResponse, Response response) {

            Log.e(TAG, response.getStatus() + "");
            Log.e(TAG, response.getReason());
                mBus.post(new UploadAvatarEvent(UploadAvatarEvent.Type.RESULT, response));

        }

        @Override
        public void failure(RetrofitError error) {
            Response response = error.getResponse();
            UploadAvatarResponse uploadAvatarResponse = new UploadAvatarResponse();
            mBus.post(new UploadAvatarEvent(UploadAvatarEvent.Type.RESULT, response));
            Log.e(TAG, response.getStatus() + "");
            Log.e(TAG, response.getReason());
        }
    };


    private class UploadImageTask extends AsyncTask<File, Void, Boolean> {

        String userAPIKey;
        int userId;

        public UploadImageTask(String userAPIKey, int userId) {
            super();
            this.userAPIKey = userAPIKey;
            this.userId = userId;
        }

        @Override
        protected Boolean doInBackground(File... params) {
            File imgFile = params[0];
            int responseCode = uploadFile(imgFile, userAPIKey, userId);
            if(responseCode == 200)  return true;
            else return false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(Boolean result) {
            if(result) mBus.post(new UploadAvatarEvent(UploadAvatarEvent.Type.UPLOAD_SUCCESSFUL, null));
            else mBus.post(new UploadAvatarEvent(UploadAvatarEvent.Type.UPLOAD_FAILED, null));
        }
    }
}
