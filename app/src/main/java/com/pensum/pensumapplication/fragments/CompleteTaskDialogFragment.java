package com.pensum.pensumapplication.fragments;

import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.helpers.FormatterHelper;
import com.pensum.pensumapplication.helpers.NotificationHelper;
import com.pensum.pensumapplication.models.Stat;
import com.pensum.pensumapplication.models.Task;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by violetaria on 8/28/16.
 */
public class CompleteTaskDialogFragment extends DialogFragment {
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.rbRating) RatingBar rbRating;
    @BindView(R.id.btnComplete) Button btnComplete;

    private Unbinder unbinder;
    private Task task;

    public CompleteTaskDialogFragment(){}

    public static CompleteTaskDialogFragment newInstance(String taskObjectId){
        CompleteTaskDialogFragment fragment = new CompleteTaskDialogFragment();
        Bundle args = new Bundle();
        args.putString("task_object_id",taskObjectId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String taskObjectId = getArguments().getString("task_object_id");
        Task task = new Task();
        task.setObjectId(taskObjectId);
        ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
        query.include("candidate");
        try {
            this.task = query.get(taskObjectId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_complete_task, parent, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateView();
    }

    private void populateView(){
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.setStatus("completed");
                task.setRating(new BigDecimal(rbRating.getRating()));
                task.saveInBackground();
                updateCandidateRating();
                NotificationHelper.sendAlert(getContext(), "Your task is complete with a rating of " + rbRating.getRating(), task.getCandidate().getObjectId());
                dismiss();
            }
        });
        ParseUser candidate = task.getCandidate();
        tvName.setText(FormatterHelper.formatName(candidate.getString("fbName")));
        String imageUrl = candidate.getString("profilePicUrl");
        if (imageUrl != null){
            Picasso.with(getContext()).load(imageUrl).
                    transform(new CropCircleTransformation()).into(ivProfileImage);
        } else {
            Picasso.with(getContext()).load(R.mipmap.ic_launcher).
                    transform(new CropCircleTransformation()).into(ivProfileImage);
        }

        if (rbRating.getProgressDrawable() instanceof LayerDrawable) {
            LayerDrawable stars = (LayerDrawable) rbRating.getProgressDrawable();
            DrawableCompat.setTint(stars.getDrawable(2), ContextCompat.getColor(getActivity(), R.color.colorAccent));
        }
        else {
            // for Android 4.3, ratingBar.getProgressDrawable()=DrawableWrapperHoneycomb
            DrawableCompat.setTint(rbRating.getProgressDrawable(), ContextCompat.getColor(getActivity(), R.color.colorAccent));
        }
    }

    private void updateCandidateRating(){
        ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
        query.whereEqualTo("candidate",task.getCandidate());
        query.whereEqualTo("status","completed");
        query.findInBackground(new FindCallback<Task>() {
            @Override
            public void done(List<Task> tasksFromQuery, ParseException e) {
                if (e == null) {
                   recalculateCandidateRating(tasksFromQuery);
                } else {
                    Log.e("message", "Error Loading Tasks" + e);
                }
            }
        });
    }


    private void recalculateCandidateRating(final List<Task> completedTasks){
        Double ratingSum = 0.0;
        for(Task t: completedTasks){
            ratingSum += t.getRating();
        }

        int totalComplete = 1;
        if (completedTasks.size() > 0)
            totalComplete = completedTasks.size();

        final BigDecimal rating = new BigDecimal(ratingSum / totalComplete);

        ParseQuery<Stat> query = ParseQuery.getQuery(Stat.class);
        query.whereEqualTo("user",task.getCandidate());
        query.getFirstInBackground(new GetCallback<Stat>() {
            @Override
            public void done(Stat stat, ParseException e) {
                if (e == null){
                    stat.setTasksCompleted(completedTasks.size());
                    stat.setRating(rating);
                    stat.saveInBackground();
                } else {
                    String message = e.getMessage();
                    if (message.toLowerCase().contains("no results found for query")) {
                        Stat newStat = new Stat();
                        newStat.setUser(task.getCandidate());
                        newStat.setTasksCompleted(completedTasks.size());
                        newStat.setRating(rating);
                        newStat.saveInBackground();
                    } else {
                        Log.e("message", "Error" + e);
                    }
                }
            }
        });
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
