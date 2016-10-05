package com.apt7.rxpermissions;

import android.os.Build;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raviteja on 05-10-2016 for RxPermission.
 * Copyright (c) 2016 Hug Innovations. All rights reserved.
 */

public class PermissionsIgnoreList {
    private static PermissionsIgnoreList permissionsIgnoreList;
    private List<String> permissionsList = new ArrayList<>();

    public static PermissionsIgnoreList getInstance() {
        if (permissionsIgnoreList == null) {
            permissionsIgnoreList = new PermissionsIgnoreList();
        }
        return permissionsIgnoreList;
    }

    private PermissionsIgnoreList() {
        setUp();
    }

    private void setUp() {
        permissionsList.clear();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            permissionsList.add("android.permission.GET_ACCOUNTS");
        }

        permissionsList.add("android.permission.ACCESS_LOCATION_EXTRA_COMMANDS");
        permissionsList.add("android.permission.ACCESS_NETWORK_STATE");
        permissionsList.add("android.permission.ACCESS_NOTIFICATION_POLICY");
        permissionsList.add("android.permission.ACCESS_WIFI_STATE");
        permissionsList.add("android.permission.ACCESS_WIMAX_STATE");
        permissionsList.add("android.permission.BLUETOOTH");
        permissionsList.add("android.permission.BLUETOOTH_ADMIN");
        permissionsList.add("android.permission.BROADCAST_STICKY");
        permissionsList.add("android.permission.CHANGE_NETWORK_STATE");
        permissionsList.add("android.permission.CHANGE_WIFI_MULTICAST_STATE");
        permissionsList.add("android.permission.CHANGE_WIFI_STATE");
        permissionsList.add("android.permission.CHANGE_WIMAX_STATE");
        permissionsList.add("android.permission.DISABLE_KEYGUARD");
        permissionsList.add("android.permission.EXPAND_STATUS_BAR");
        permissionsList.add("android.permission.FLASHLIGHT");
        permissionsList.add("android.permission.GET_PACKAGE_SIZE");
        permissionsList.add("android.permission.INTERNET");
        permissionsList.add("android.permission.KILL_BACKGROUND_PROCESSES");
        permissionsList.add("android.permission.MODIFY_AUDIO_SETTINGS");
        permissionsList.add("android.permission.NFC");
        permissionsList.add("android.permission.READ_SYNC_SETTINGS");
        permissionsList.add("android.permission.READ_SYNC_STATS");
        permissionsList.add("android.permission.RECEIVE_BOOT_COMPLETED");
        permissionsList.add("android.permission.REORDER_TASKS");
        permissionsList.add("android.permission.REQUEST_INSTALL_PACKAGES");
        permissionsList.add("android.permission.SET_TIME_ZONE");
        permissionsList.add("android.permission.SET_WALLPAPER");
        permissionsList.add("android.permission.SET_WALLPAPER_HINTS");
        permissionsList.add("android.permission.SUBSCRIBED_FEEDS_READ");
        permissionsList.add("android.permission.TRANSMIT_IR");
        permissionsList.add("android.permission.USE_FINGERPRINT");
        permissionsList.add("android.permission.VIBRATE");
        permissionsList.add("android.permission.WAKE_LOCK");
        permissionsList.add("android.permission.WRITE_SYNC_SETTINGS");
        permissionsList.add("com.android.alarm.permission.SET_ALARM");
        permissionsList.add("com.android.launcher.permission.INSTALL_SHORTCUT");
        permissionsList.add("com.android.launcher.permission.UNINSTALL_SHORTCUT");
    }

    public boolean validate(String permission) {
        if (permission == null) {
            throw new NullPointerException("Permission cant be null");
        }
        return !permissionsList.contains(permission);
    }

    public List<String> validate(List<String> pList) {
        if (pList == null) {
            throw new NullPointerException("List cant be null");
        }
        List<String> processedList = new ArrayList<>();
        for (String permission : pList) {
            if (permission != null && !permissionsList.contains(permission)) {
                processedList.add(permission);
            }
        }
        return processedList;
    }
}
