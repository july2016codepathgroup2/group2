package com.pensum.pensumapplication.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.adapters.ConversationAdapter;
import com.pensum.pensumapplication.models.Conversation;
import com.pensum.pensumapplication.models.Task;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by violetaria on 8/24/16.
 */
public class ConversationFragment extends Fragment {
    @BindView(R.id.rvConversations) RecyclerView rvConversations;
    @BindView(R.id.ivProfilePicture) ImageView ivProfilePicture;
    @BindView(R.id.tvType) TextView tvType;
    @BindView(R.id.tvTaskTitle) TextView tvTaskTitle;
    @BindView(R.id.tvPrice) TextView tvPrice;

    public ConversationAdapter adapter;
    public ArrayList<Conversation> conversations;
    private Unbinder unbinder;
    private Task task;

    public ConversationFragment(){

    }

    public static ConversationFragment newInstance(String taskId) {
        ConversationFragment fragment = new ConversationFragment();
        Bundle args = new Bundle();
        args.putString("task_id", taskId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conversations = new ArrayList<>();
        adapter = new ConversationAdapter(getContext(),conversations);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversations, container, false);
        unbinder = ButterKnife.bind(this, view);

        adapter.setOnItemClickListener(new ConversationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                // TODO build this out
                //showConversationDetailFragment(conversaionts.get(position));
            }
        });

        rvConversations.setAdapter(adapter);
        rvConversations.setLayoutManager(new LinearLayoutManager(getContext()));


        String taskId = getArguments().getString("task_id");
        ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK); // or CACHE_ONLY
        query.include("posted_by");
        query.getInBackground(taskId, new GetCallback<Task>() {
            public void done(Task item, ParseException e) {
                if (e == null) {
                    task = item;
                    tvType.setText("#" + task.getType());
                    tvPrice.setText(NumberFormat.getCurrencyInstance().format(task.getBudget()));
                    tvTaskTitle.setText(task.getTitle());
                    String imageUrl = task.getPostedBy().getString("profilePicUrl");
                    if (imageUrl != null){
                        Picasso.with(getContext()).load(imageUrl).
                                transform(new CropCircleTransformation()).into(ivProfilePicture);
                    } else {
                        Picasso.with(getContext()).load(R.mipmap.ic_launcher).
                                transform(new CropCircleTransformation()).into(ivProfilePicture);
                    }
                    populateConversations();
                }
            }
        });
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void populateConversations() {
        List<ParseQuery<Conversation>> subConversationQueries = new ArrayList<>();

        ParseQuery<Conversation> ownerQuery = ParseQuery.getQuery("Conversation");
        ownerQuery.whereEqualTo("owner", ParseUser.getCurrentUser());
        ownerQuery.whereEqualTo("task", task);

        subConversationQueries.add(ownerQuery);
        ParseQuery<Conversation> candidateQuery = ParseQuery.getQuery("Conversation");
        candidateQuery.whereEqualTo("candidate", ParseUser.getCurrentUser());
        candidateQuery.whereEqualTo("task", task);

        subConversationQueries.add(candidateQuery);
        ParseQuery<Conversation> mainConversationQuery = ParseQuery.getQuery("Conversation").
                or(subConversationQueries).include("task").include("candidate").include("posted_by");
        mainConversationQuery.findInBackground(new FindCallback<Conversation>() {
            public void done(List<Conversation> conversationsFromQuery, ParseException e) {
                if (e == null) {
                    int previousContentSize = conversations.size();
                    conversations.clear();
                    adapter.notifyItemRangeRemoved(0, previousContentSize);
                    conversations.addAll(conversationsFromQuery);
                    adapter.notifyItemRangeInserted(0, conversationsFromQuery.size());
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }

}
