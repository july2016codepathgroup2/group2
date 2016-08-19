package com.pensum.pensumapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    public static final List<String> permissions = new ArrayList<String>() {{
        add("public_profile");
        add("email");
    }};

    Button btnFbLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnFbLogin = (Button) findViewById(R.id.btnFbLogin);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void setBtnFbLoginOnClickListener() {
        btnFbLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this,
                        permissions, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if( user == null ) {
                                    Toast.makeText(getApplicationContext(),
                                            "User cancelled FB login", Toast.LENGTH_SHORT).show();
                                } else if(user.isNew()) {
                                    Toast.makeText(getApplicationContext(),
                                            "User signed up FB login", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "User logged in through FB", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            }
        });
    }
}
