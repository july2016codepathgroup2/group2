package com.pensum.pensumapplication.helpers;

import android.content.Context;
import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by violetaria on 9/4/16.
 */
public class NotificationHelper {

    public static void sendAlert(Context context, String message, String userObjectId) {

        final Map<String, Object> params = new HashMap<>();
        params.put("message", message);
        params.put("userObjectId", userObjectId);

        ParseCloud.callFunctionInBackground("pushAlertToUser", params, new FunctionCallback<String>() {
            public void done(String result, ParseException e) {
                if (e == null) {
                    Log.d("DEBUG","ANNOUNCEMENT SUCCESS");
                } else {
                    Log.e("ERROR","ANNOUNCEMENT FAILURE");
                }
            }
        });
    }
}
