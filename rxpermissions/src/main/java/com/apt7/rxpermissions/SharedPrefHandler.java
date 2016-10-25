package com.apt7.rxpermissions;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Raviteja on 25-10-2016 for RxPermissions.
 * Copyright (c) 2016 Hug Innovations. All rights reserved.
 */

public class SharedPrefHandler {
    public static final String PREFERENCES = "rx_permissions_pref";
    private static SharedPrefHandler ourInstance;

    private SharedPrefHandler() {
    }

    public static synchronized SharedPrefHandler getInstance() {
        if (ourInstance == null) {
            ourInstance = new SharedPrefHandler();
        }
        return ourInstance;
    }

    public void setPref(Context mContext, String permission, boolean denied) {
        if (mContext == null) {
            return;
        }
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(permission, denied);
        editor.apply();
    }

    public void resetPref(Context mContext, String permission) {
        if (mContext == null) {
            return;
        }
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(permission);
        editor.apply();
    }

    public boolean getPref(Context mContext, String permission) {
        if (mContext == null) {
            return false;
        }
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean(permission, false);
    }
}
