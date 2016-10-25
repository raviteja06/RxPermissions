package com.apt7.rxpermissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Raviteja on 04-10-2016 for RxPermission.
 * Copyright (c) 2016 Hug Innovations. All rights reserved.
 */

/*
 * Permission observable will make asking the permission in marshmallow easy and seamless by using RxJava/RxAndroid
 * Idea of using PublishSubject and having shadow activity is taken from RxPermission (https://github.com/tbruyelle/RxPermissions)
 * This library has few improvements to it.
 * It will return whether the permission is revoked or not.
 * Simplified request and requestAsTransformer.
 * also added check permissions in advanced as an observable.
 * This uses RxJava 2
 */
public class PermissionObservable {

    private static PermissionsIgnoreList permissionsIgnoreList;
    private static PermissionObservable permissionObservable;
    private Map<String, PublishSubject<Permission>> publishSubjectHashMap = new HashMap<>();
    private static SharedPrefHandler sharedPrefHandler;

    private PermissionObservable() {
    }

    /*
     * returns the instance of the observable. (Singleton)
     */
    public static PermissionObservable getInstance() {
        if (permissionObservable == null) {
            permissionObservable = new PermissionObservable();
            permissionsIgnoreList = PermissionsIgnoreList.getInstance();
            sharedPrefHandler = SharedPrefHandler.getInstance();
        }
        return permissionObservable;
    }

    /*
     * Check the permission status. It returns permission object when subscribed to this observable.
     * Has error check for permissions and context. NPE is thrown if found.
     */
    public Observable<Permission> checkThePermissionStatus(final Context context, String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return Observable.error(new IllegalArgumentException("request/requestEach requires at least one input permission"));
        }
        if (context == null) {
            return Observable.error(new NullPointerException("Context cant be null"));
        }
        // for each permission, pass to flap map to have specific observable to return permission status.
        return Observable.fromArray(permissions)
                .flatMap(new Function<String, Observable<Permission>>() {
                    @Override
                    public Observable<Permission> apply(final String permission) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<Permission>() {
                            @Override
                            public void subscribe(ObservableEmitter<Permission> e) throws Exception {
                                e.onNext(new Permission(permission, permissionsIgnoreList.validate(permission) ? checkPermission(context, permission) : Permission.PERMISSION_GRANTED));
                                e.onComplete();
                            }
                        });
                    }
                });
    }

    /*
     * Makes the permission request on a shadow activity. It returns permission object when subscribed to this observable.
     * Has error check for permissions and context. NPE is thrown if found.
     */
    public Observable<Permission> request(final Context context, final String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return Observable.error(new IllegalArgumentException("request/requestEach requires at least one input permission"));
        }
        if (context == null) {
            return Observable.error(new NullPointerException("Context cant be null"));
        }
        // validates and send the list to flat map, so it can divide as multiple observable calls
        return Observable.just(permissionsIgnoreList.validate(validate(context, permissions)))
                // for each permission, pass to flap map to have specific observable to return response for permission request.
                .flatMap(new Function<List<String>, Observable<Permission>>() {
                    @Override
                    public Observable<Permission> apply(List<String> permissions) throws Exception {
                        // request all the permissions on shadow activity
                        requestPermission(context, permissions.toArray(new String[permissions.size()]));
                        // for each permission, call flap map to have specific observable to return response for permission request.
                        return Observable.fromIterable(permissions).flatMap(new Function<String, Observable<Permission>>() {
                            @Override
                            public Observable<Permission> apply(String permission) throws Exception {
                                return responseHolder(permission);
                            }
                        });
                    }
                });
    }

    /*
     * Use this when you are making call from RxBinding or Observable.compose(requestAsTransformer).
     * Makes the permission request on a shadow activity. It returns permission object when subscribed to this observable.
     * Has error check for permissions and context. NPE is thrown if found.
     */
    public ObservableTransformer<Object, Permission> requestAsTransformer(final Context context, final String... permissions) {
        checkForNull(context, permissions);
        return new ObservableTransformer<Object, Permission>() {
            @Override
            public Observable<Permission> apply(Observable<Object> o) {
                return request(context, permissions);
            }
        };
    }

    /*
     * return the subscriber subject for subscriber call back
     */
    private Observable<Permission> responseHolder(final String permission) {
        if (!publishSubjectHashMap.containsKey(permission)) {
            return getJustNull();
        }
        return publishSubjectHashMap.get(permission);
    }

    /*
    * Return a null observable
    */
    private Observable<Permission> getJustNull() {
        return Observable.create(new ObservableOnSubscribe<Permission>() {
            @Override
            public void subscribe(ObservableEmitter<Permission> e) throws Exception {
                e.onNext(new Permission("", 0));
            }
        });
    }

    /*
     * for every permission, have a PublishSubject so they can be tracked back. when there is any response.
     */
    private void requestPermission(Context context, String[] permissions) {
        for (String permission : permissions) {
            PublishSubject<Permission> subject = publishSubjectHashMap.get(permission);
            if (subject == null) {
                subject = PublishSubject.create();
                publishSubjectHashMap.put(permission, subject);
            }
        }
        startShadowActivity(context, permissions);
    }

    /*
     * validates permission list and removed if the permission is already added or revoked
     */
    private List<String> validate(Context context, String... permissions) {
        List<String> requestList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : getPermissionStatus(context, permissions).entrySet()) {
            if (entry.getValue() == 0) {
                requestList.add(entry.getKey());
            }
        }
        return requestList;
    }

    /*
     * Start shadow activity. pass permissions in intent.
     */
    private void startShadowActivity(Context context, String[] permissions) {
        if (permissions != null && permissions.length > 0) {
            Intent intent = new Intent(context, ShadowActivity.class);
            intent.putExtra("permissions", permissions);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /*
     * if OS os marshmallow, return if the permission is granted or not.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private boolean isGranted(Context context, String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    /*
     * if OS os marshmallow, return if the permission is revoked or not.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private boolean isRevoked(Context context, String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || sharedPrefHandler.getPref(context, permission);
    }

    /*
     * converts string of permissions to map with status of the specific permission.
     */
    private Map<String, Integer> getPermissionStatus(Context context, String... permissions) {
        Map<String, Integer> permissionMap = new HashMap<>();
        for (String permission : permissions) {
            permissionMap.put(permission, checkPermission(context, permission));
        }
        return permissionMap;
    }

    /*
     * check for null. If true, throws NPE.
     */
    private void checkForNull(Context context, String... permissions) {
        if (context == null) {
            throw new NullPointerException("Context cant be null");
        } else if (permissions == null) {
            throw new NullPointerException("Permission cant be null");
        }
    }

    /*
     * check whether permission is granted or not.
     */
    private int checkPermission(Context context, String permission) {
        if (isGranted(context, permission)) {
            sharedPrefHandler.resetPref(context, permission);
            return Permission.PERMISSION_GRANTED;
        } else if (isRevoked(context, permission)) {
            return Permission.PERMISSION_REVOKED;
        } else {
            return Permission.PERMISSION_NOT_GRANTED;
        }
    }

    /*
     * Hook for shadow activity for returning requested data to permission observable
     */
    void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Permission.REQUEST_VALUE) {
            for (int i = 0, size = permissions.length; i < size; i++) {
                PublishSubject<Permission> subject = publishSubjectHashMap.get(permissions[i]);
                if (subject == null) {
                    throw new IllegalStateException("onRequestPermissionsResult invoked but didn't find the corresponding permission request.");
                }
                publishSubjectHashMap.remove(permissions[i]);
                boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                if (!granted) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!activity.shouldShowRequestPermissionRationale(permissions[i])) {
                            sharedPrefHandler.setPref(activity, permissions[i], true);
                        }
                    }
                }
                subject.onNext(new Permission(permissions[i], granted ? Permission.PERMISSION_GRANTED : Permission.PERMISSION_NOT_GRANTED));
                subject.onComplete();
            }
        }
    }
}

