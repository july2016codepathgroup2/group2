package com.pensum.pensumapplication.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.helpers.FormatterHelper;
import com.pensum.pensumapplication.models.Conversation;
import com.pensum.pensumapplication.models.Message;
import com.pensum.pensumapplication.models.Task;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by violetaria on 8/21/16.
 */

public class ContactOwnerFragment extends DialogFragment {
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvBudget)
    TextView tvBudget;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.etMessage)
    EditText etMessage;
    @BindView(R.id.btnSend)
    Button btnSend;
    @BindView(R.id.btnCancel)
    Button btnCancel;
    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;
    @BindView(R.id.etOffer)
    EditText etOffer;
    private Unbinder unbinder;
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
        query.include("posted_by");
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
        View view = inflater.inflate(R.layout.fragment_contact_owner, parent, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(view);
            }
        });

        etOffer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                etOffer.removeTextChangedListener(this);
                String formatted = FormatterHelper.formatMoney(s);
                etOffer.setText(formatted);
                etOffer.setSelection(formatted.length());
                etOffer.addTextChangedListener(this);
            }
        });
    }

    private void populateViews() {
        tvTitle.setText(task.getTitle());
        tvBudget.setText(NumberFormat.getCurrencyInstance().format(task.getBudget()));
        postedBy = task.getPostedBy();
        tvName.setText(FormatterHelper.formatName(postedBy.getString("fbName")));
        String imageUrl = postedBy.getString("url");
        if (imageUrl != null) {
            Picasso.with(getContext()).load(imageUrl).
                    transform(new CropCircleTransformation()).into(ivProfileImage);
        } else {
            Picasso.with(getContext()).load(R.mipmap.ic_launcher).
                    transform(new CropCircleTransformation()).into(ivProfileImage);
        }
    }

    public void sendMessage(View view) {
        ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);
        query.whereEqualTo("candidate", ParseUser.getCurrentUser());
        query.whereEqualTo("task", task);
        // TODO figure out which cache policy to use here
        //query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY); // or CACHE_ONLY
        query.getFirstInBackground(new GetCallback<Conversation>() {
            public void done(Conversation conversationFromQuery, ParseException e) {
                if (e == null) {
                    createMessage(conversationFromQuery);
                } else {
                    String message = e.getMessage();
                    if (message.toLowerCase().contains("no results found for query")) {
                        Conversation c;
                        c = new Conversation();
                        c.setTask(task);
                        c.setCandidate(ParseUser.getCurrentUser());
                        c.setOwner(postedBy);
                        c.saveInBackground();
                        createMessage(c);
                    } else {
                        Log.e("message", "Error Loading Messages" + e);
                    }
                }
            }
        });
    }

    private void createMessage(Conversation c) {
        Message message = new Message();
        c.setUnreadOwnerMessageFlag(true);
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        try {
            c.setOffer(new BigDecimal(nf.parse(etOffer.getText().toString()).toString()));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        c.saveInBackground();
        message.setFrom(ParseUser.getCurrentUser());
        message.setTo(postedBy);
        message.setConversation(c);
        message.setMessage(etMessage.getText().toString());
        message.saveInBackground();
        dismiss();
    }

}
