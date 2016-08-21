package com.pensum.pensumapplication.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.models.Message;
import com.pensum.pensumapplication.models.Task;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by violetaria on 8/21/16.
 */
public class ContactOwnerFragment extends DialogFragment {
    private TextView tvTitle;
    private TextView tvBudget;
    private TextView tvName;
    private EditText etMessage;
    private Button btnSend;
    private ImageView ivProfileImage;
    private Task task;
    private ParseUser postedBy;

    public static ContactOwnerFragment newInstance(String taskObjectId) {
        ContactOwnerFragment fragment = new ContactOwnerFragment();
        Bundle args = new Bundle();
        args.putString("task_object_id", taskObjectId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String taskObjectId = getArguments().getString("task_object_id");
        ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK); // or CACHE_ONLY
        query.getInBackground(taskObjectId, new GetCallback<Task>() {
            public void done(Task item, ParseException e) {
                if (e == null) {
                    task = item;
                    populateViews();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_owner, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvBudget = (TextView) view.findViewById(R.id.tvBudget);
        tvName = (TextView) view.findViewById(R.id.tvName);
        etMessage = (EditText) view.findViewById(R.id.etMessage);
        ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
        btnSend = (Button) view.findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(view);
            }
        });
    }

    private void populateViews(){
        tvTitle.setText(task.getTitle());
        tvBudget.setText("$" + task.getBudget().toString());
        try{
            postedBy = task.getPostedBy().fetchIfNeeded();
            tvName.setText(postedBy.getString("fbName"));
            ParseFile profileImage = postedBy.getParseFile("profileThumb");
            if(profileImage != null){
                String imageUrl = profileImage.getUrl();
                Picasso.with(getContext()).load(imageUrl).
                        transform(new CropCircleTransformation()).into(ivProfileImage);
            } else {
                Picasso.with(getContext()).load(R.mipmap.ic_launcher).
                        transform(new CropCircleTransformation()).into(ivProfileImage);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(View view){
        Message message = new Message();
        message.setFrom(ParseUser.getCurrentUser());
        message.setTo(postedBy);
        message.setTask(task);
        message.setMessage(etMessage.getText().toString());
        message.saveInBackground();
        dismiss();
    }

}
