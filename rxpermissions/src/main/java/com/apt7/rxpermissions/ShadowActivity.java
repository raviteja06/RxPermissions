package com.apt7.rxpermissions;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

/**
 * Created by Raviteja on 04-10-2016 for RxPermission.
 * Copyright (c) 2016 Hug Innovations. All rights reserved.
 */

public class ShadowActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    // Read new intent when received.
    private void handleIntent(Intent intent) {
        String[] permissions = intent.getStringArrayExtra("permissions");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, Permission.REQUEST_VALUE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // pass the values to permission observable
        PermissionObservable.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
        // finish activity
        finish();
    }
}