package de.tum.mitfahr.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

/**
 * Created by abhijith on 25/11/14.
 */
public class AbstractService {

    public SharedPreferences mSharedPreferences;

    public AbstractService(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
}
