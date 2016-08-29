package com.jk.examenma15;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by jk on 29/08/16.
 */
public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }

}

