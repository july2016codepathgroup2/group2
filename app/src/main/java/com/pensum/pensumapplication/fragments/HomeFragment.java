package com.pensum.pensumapplication.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;

/**
 * Created by eddietseng on 8/19/16.
 */
public class HomeFragment extends Fragment {
    TextView tvName;
    TextView tvEmail;
    ImageView ivProfilePic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_test, container, false);

        tvName = (TextView) view.findViewById(R.id.tvName);
        tvEmail = (TextView) view.findViewById(R.id.tvEmail);
        ivProfilePic = (ImageView) view.findViewById(R.id.ivProfilePic);

        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            ivProfilePic.setImageResource(0);
            tvName.setText(user.getUsername());

            String fbName = (String) user.get("fbName");
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

        return view;
    }
}