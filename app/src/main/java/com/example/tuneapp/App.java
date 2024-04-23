package com.example.tuneapp;

import android.app.Application;
import android.content.Context;
import com.google.firebase.FirebaseApp;

public class App extends Application {
    private static App instance;

    public static Context getAppContext() {
        // Ensure instance is returned as a Context
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
    }
}
