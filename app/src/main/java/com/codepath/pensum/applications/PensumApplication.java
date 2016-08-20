package com.codepath.pensum.applications;

import android.app.Application;

import com.codepath.pensum.models.Task;
import com.codepath.pensum.models.User;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;

/**
 * Created by violetaria on 8/17/16.
 */
public class PensumApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Task.class);
        ParseObject.registerSubclass(User.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("pensum") // should correspond to APP_ID env variable
                .addNetworkInterceptor(new ParseLogInterceptor())
                .clientKey("OddqencJKEToTctEbXRCbYHgDQkxex")
                .server("https://pensumapi.herokuapp.com/parse").build());
    }
}

