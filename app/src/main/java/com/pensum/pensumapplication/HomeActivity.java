package com.pensum.pensumapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseUser;

public class HomeActivity extends AppCompatActivity {
    TextView tvName;
    TextView tvEmail;
    ImageView ivProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvName = (TextView) findViewById(R.id.tvName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        ivProfilePic = (ImageView)findViewById(R.id.ivProfilePic);

        ParseUser user = ParseUser.getCurrentUser();
        if(user != null ) {
            ivProfilePic.setImageResource(0);
            tvName.setText(user.getUsername());

            String fbName = (String)user.get("fbName");
            tvEmail.setText(fbName);

            try {
                ParseFile parseFile = user.getParseFile("profileThumb");
                byte[] data = parseFile.getData();
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                ivProfilePic.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
