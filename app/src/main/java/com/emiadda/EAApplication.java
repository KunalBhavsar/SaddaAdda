package com.emiadda;

import android.app.Application;

import com.emiadda.utils.AppPreferences;

/**
 * Created by Kunal on 06/07/16.
 */
public class EAApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppPreferences.init(this);
    }
}
