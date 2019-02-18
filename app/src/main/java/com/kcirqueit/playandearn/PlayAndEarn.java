package com.kcirqueit.playandearn;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class PlayAndEarn extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
