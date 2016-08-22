package com.pensum.pensumapplication.applications;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.models.Conversation;
import com.pensum.pensumapplication.models.Message;
import com.pensum.pensumapplication.models.Skill;
import com.pensum.pensumapplication.models.Task;

/**
 * Created by violetaria on 8/17/16.
 */
public class PensumApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Task.class);
        ParseObject.registerSubclass(Conversation.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(Skill.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.parse_app_id)) // should correspond to APP_ID env variable
                .addNetworkInterceptor(new ParseLogInterceptor())
                .clientKey(getString(R.string.parse_client_key))
                .server("https://pensumapi.herokuapp.com/parse").build());

        ParseFacebookUtils.initialize(this);

    }
}

