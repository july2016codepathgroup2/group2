package com.pensum.pensumapplication.fragments.profile;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.models.Stat;

/**
 * Created by eddietseng on 8/21/16.
 */
public class StatusFragment  extends Fragment {
    private CircularProgressBar circularProgressBar;
    private TextView tvCompPercent;

    private TextView tvRatingNumber;
    private RatingBar rbStatus;
    private TextView tvTotalRater;

    private Stat stat;

    public static StatusFragment newInstance(String userId) {
        StatusFragment frag = new StatusFragment();
        Bundle args = new Bundle();
        if(userId!=null)
            args.putString("userId", userId);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile_status, container, false);

        View completeSection = view.findViewById(R.id.taskCompProgress);
        circularProgressBar = (CircularProgressBar)completeSection.findViewById(R.id.cpbCompPercent);
        tvCompPercent = (TextView)completeSection.findViewById(R.id.tvCompPercent);

        View ratingSection = view.findViewById(R.id.ratingStatus);
        tvRatingNumber = (TextView)ratingSection.findViewById(R.id.tvRatingNumber);
        rbStatus = (RatingBar)ratingSection.findViewById(R.id.rbStatus);
        View taskRating = ratingSection.findViewById(R.id.taskRating);
        tvTotalRater = (TextView)taskRating.findViewById(R.id.tvTotalRater);

        String userId = (String)getArguments().get("userId");
        if(userId!=null) {
            // Construct query to execute
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.getInBackground(userId, new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser object, ParseException e) {
                    if (e == null) {
                        stat = (Stat) object.get("stats");
                        setStatus();
                    } else {
                        Log.e("message", "Error getting user in profile Status" + e);
                    }
                }
            });
        } else {
            stat = (Stat) ParseUser.getCurrentUser().get("stats");
            setStatus();
        }

        return view;
    }

    public void setStatus() {
        try {
            stat.fetchIfNeeded();

            float percent = (float)(stat.getTasksCompleted()/stat.getTasksCompleted()); //TODO need and extra field
            int animationDuration = 2500;
            circularProgressBar.setProgressWithAnimation(percent*100, animationDuration);
            tvCompPercent.setText(percent*100+"%");

            tvRatingNumber.setText(String.format("%1.1f", stat.getRating()));
            tvTotalRater.setText(stat.getTasksCompleted()+" total");

            Drawable progress = rbStatus.getProgressDrawable();
            DrawableCompat.setTint(progress, ContextCompat.getColor(getContext(), R.color.colorAccent));
            rbStatus.setProgressDrawable(progress);
            rbStatus.setRating(stat.getRating().floatValue());
            rbStatus.setEnabled(false);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
