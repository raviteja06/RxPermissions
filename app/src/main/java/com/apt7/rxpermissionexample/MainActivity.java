package com.apt7.rxpermissionexample;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.apt7.rxpermissions.Permission;
import com.apt7.rxpermissions.PermissionObservable;

import io.reactivex.observers.DisposableObserver;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Check the permissions are given or not using below code.
        // returns 1,0,-1 as Granted, Not Granted, Revoked
        PermissionObservable.getInstance().checkThePermissionStatus(this,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_SMS, Manifest.permission.ACCESS_WIFI_STATE)
                .subscribe(new DisposableObserver<Permission>() {
                    @Override
                    public void onNext(Permission permission) {
                        System.out.println("Permission Check : " + permission.getName() + " -- " + permission.getGranted());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        System.out.println("DONE");
                        // on complete, dispose method automatically un subscribes the subscriber
                        dispose();
                    }
                });

        // REQUESTS the permissions using below code.
        // returns 1,0 as Granted, Not Granted
        // This method has check for not requesting revoked and granted permissions.
        PermissionObservable.getInstance().request(this,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_SMS, Manifest.permission.ACCESS_WIFI_STATE)
                .subscribe(new DisposableObserver<Permission>() {

                    @Override
                    public void onNext(Permission permission) {
                        System.out.println("Permission Request  : " + permission.getName() + " -- " + permission.getGranted());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        System.out.println("DONE");
                        // on complete, dispose method automatically un subscribes the subscriber
                        dispose();
                    }
                });
    }
}
