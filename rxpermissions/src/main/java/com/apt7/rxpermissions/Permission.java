package com.apt7.rxpermissions;

/**
 * Created by Raviteja on 04-10-2016 for RxPermissionExample.
 * Copyright (c) 2016 Hug Innovations. All rights reserved.
 */

public class Permission {
    /*
     * Permission name
     */
    private String name;

    /*
     * granted status
     */
    private int granted;

    public String getName() {
        return name;
    }

    public int getGranted() {
        return granted;
    }

    // Permission is granted
    public static final int PERMISSION_GRANTED = 1;
    // Permission is not granted
    public static final int PERMISSION_NOT_GRANTED = 0;
    // Permission is revoked (show a custom pop up for this cases)
    public static final int PERMISSION_REVOKED = -1;
    static final int REQUEST_VALUE = 42;

    public Permission(String name, int granted) {
        this.name = name;
        this.granted = granted;
    }

    private Permission() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return granted == that.granted && name.equals(that.name);
    }

    @Override
    public String toString() {
        return "Permission{" +
                "name='" + name + '\'' +
                ", granted=" + granted +
                '}';
    }
}