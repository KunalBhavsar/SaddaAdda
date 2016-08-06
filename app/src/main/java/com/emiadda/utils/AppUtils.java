package com.emiadda.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Shraddha on 6/8/16.
 */
public class AppUtils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }
}
