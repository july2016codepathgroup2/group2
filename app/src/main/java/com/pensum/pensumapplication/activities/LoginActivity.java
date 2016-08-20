package com.pensum.pensumapplication.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pensum.pensumapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
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
    Bitmap bitmap;

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

        //Saving profile photo as a ParseFile
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            byte[] data = stream.toByteArray();
            String thumbName = name.replaceAll("\\s+", "");
            final ParseFile parseFile = new ParseFile(thumbName + "_thumb.jpg", data);

            parseFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    user.put("profileThumb", parseFile);

                    //Finally save all the user details
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Toast.makeText(LoginActivity.this, "New user:" + name + " Signed up", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        }
    }

    private void getUserDetailsFromFB() {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,picture");

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            Log.d("Response", response.getRawResponse());

                            name = response.getJSONObject().getString("name");

                            JSONObject picture = response.getJSONObject().getJSONObject("picture");
                            JSONObject data = picture.getJSONObject("data");

                            //  Returns a 50x50 profile picture
                            String pictureUrl = data.getString("url");

                            Log.d("Profile pic", "url: " + pictureUrl);

                            new ProfilePhotoAsync(pictureUrl).execute();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    public class ProfilePhotoAsync extends AsyncTask<String, String, String> {
        String url;

        public ProfilePhotoAsync(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(String... params) {
            // Fetching data from URI and storing in bitmap
            bitmap = DownloadImageBitmap(url);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            userDataUpdate();
        }
    }

    public static Bitmap DownloadImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("IMAGE", "Error getting bitmap", e);
        }
        return bm;
    }
}
