package com.pensum.pensumapplication.helpers;

import android.content.Context;
import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.pensum.pensumapplication.models.Task;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by violetaria on 9/4/16.
 */
public class NotificationHelper {

    public static void sendBidAlert(Context context, String message, Task task) {

        final Map<String, Object> params = new HashMap<>();
        params.put("message", message);
        params.put("postOwnerId", ParseUser.getCurrentUser().getObjectId());
        params.put("useMasterKey", true);//Must have this line

        ParseCloud.callFunctionInBackground("pushBidReceived", params, new FunctionCallback<String>() {
            public void done(String result, ParseException e) {
                if (e == null) {
                    Log.d("DEBUG","ANNOUNCEMENT SUCCESS");
                } else {
                    Log.e("ERROR","ANNOUNCEMENT FAILURE");
                }
            }
        });

//        ParsePush push = new ParsePush();
//        push.setChannel("parse_user_channel_" + ParseUser.getCurrentUser().getObjectId());
//        push.setMessage("The Giants just scored! It's now 2-2 against the Mets.");
//        push.sendInBackground();

    }
}
