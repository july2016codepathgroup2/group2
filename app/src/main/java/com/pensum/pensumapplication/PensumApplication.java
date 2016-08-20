package com.pensum.pensumapplication;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.interceptors.ParseLogInterceptor;

/**
 * Created by eddietseng on 8/19/16.
 */
public class PensumApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // set applicationId and server based on the values in the Heroku settings.
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("pensum") // should correspond to APP_ID env variable
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server("https://pensumapi.herokuapp.com/parse/")
                .build());

        ParseFacebookUtils.initialize(this);
    }
}
