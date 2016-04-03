package de.tum.mitfahr.networking;

import android.util.Base64;
import android.util.Log;

import java.security.Security;

import de.tum.mitfahr.util.Crypto;

/**
 * Created by abhijith on 09/05/14.
 */
public class BackendUtil {

    public static String getLoginHeader(String username, String password) {
        //String credentials = username.trim() + ":" + Crypto.sha512(password.trim()); //api v2
        //TODO:the following is for api v3
        String credentials = username.trim() + ":" + password.trim();
        String authStr = "Basic "+ Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        return authStr;
    }


    public static String getAPIKey() {
        return null;
    }
}
