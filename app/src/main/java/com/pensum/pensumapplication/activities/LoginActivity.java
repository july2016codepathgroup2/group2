package com.pensum.pensumapplication.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pensum.pensumapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    public static final List<String> permissions = new ArrayList<String>() {{
        add("public_profile");
        add("email");
    }};

    ImageButton btnFbLogin;
    ProgressDialog pd;

    ParseUser user;
    String name;
    String profilePicUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();
        }

        btnFbLogin = (ImageButton) findViewById(R.id.btnFbLogin);

        pd = new ProgressDialog(this);
        pd.setTitle("Loading...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);

        // Get the key hash for facebook
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.pensum.pensumapplication",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
//
//        }

        setBtnFbLoginOnClickListener();
    }

    private void startWithCurrentUser() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void setBtnFbLoginOnClickListener() {
        btnFbLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this,
                        permissions, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if( user == null ) {
                                    Toast.makeText(getApplicationContext(),
                                            "User cancelled FB login", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                } else if(user.isNew()) {
                                    Toast.makeText(getApplicationContext(),
                                            "User signed up FB login", Toast.LENGTH_SHORT).show();
                                    getUserDetailsFromFB();
                                    pd.dismiss();
                                    startWithCurrentUser();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "User logged in through FB", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                    startWithCurrentUser();
                                }
                            }
                        }
                );
            }
        });
    }

    private void userDataUpdate() {
        user = ParseUser.getCurrentUser();
        user.put("fbName", name);
        user.put("profilePicUrl", profilePicUrl);

        //Finally save all the user details
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(LoginActivity.this,
                        "New user:" + name + " Signed up", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserDetailsFromFB() {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name");

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            Log.d("Profile Response", response.getRawResponse());

                            name = response.getJSONObject().getString("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

        parameters.remove("fields");
        parameters.putString("type", "large");
        parameters.putString("redirect", "false");

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/picture",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            Log.d("Profile Pic Response", response.getRawResponse());

                            JSONObject data = response.getJSONObject().getJSONObject("data");

                            String pictureUrl = data.getString("url");

                            Log.d("Profile pic", "url: " + pictureUrl);
                            profilePicUrl = pictureUrl;

//                            new ProfilePhotoAsync(pictureUrl).execute();
                            userDataUpdate();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }
}
