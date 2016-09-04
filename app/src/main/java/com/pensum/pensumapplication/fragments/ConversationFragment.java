package com.pensum.pensumapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
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
    @BindView(R.id.pbConversation) ProgressBar pbConversation;

    public ConversationAdapter adapter;
    public ArrayList<Conversation> conversations;
    private Unbinder unbinder;
    private Task task;
    private OnConversationAcceptedListener listener;

    public interface OnConversationAcceptedListener{
        void launchChatFragment(Conversation conversation);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnConversationAcceptedListener) {
            listener = (OnConversationAcceptedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ConversationFragment.OnConversationAcceptedListener");
        }
    }

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

        String taskId = getArguments().getString("task_id");
        ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
//        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE); // or CACHE_ONLY
        query.include("posted_by");
        try {
            task = query.get(taskId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversations, container, false);
        unbinder = ButterKnife.bind(this, view);

        rvConversations.setAdapter(adapter);
        rvConversations.setLayoutManager(new LinearLayoutManager(getContext()));


        ItemTouchHelper.SimpleCallback simpleItemTouchCallback
                = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                final Conversation c = conversations.get(position);
                if(swipeDir == ItemTouchHelper.LEFT){
                    conversations.remove(position);
                    adapter.notifyItemRemoved(position);
                    if (conversations.size() == 0) {
                        c.getTask().setHasBidder(false);
                        c.getTask().saveInBackground();
                    }
                    c.setStatus("declined");
                    c.saveInBackground();
                    Snackbar.make(rvConversations, R.string.snackbar_declined, Snackbar.LENGTH_LONG).setAction(R.string.snackbar_undo, new View.OnClickListener(){
                        @Override
                        public void onClick(View view){
                            conversations.add(position, c);
                            adapter.notifyItemInserted(position);
                            rvConversations.scrollToPosition(position);
                            c.getTask().setHasBidder(true);
                            c.getTask().saveInBackground();
                            c.setStatus("bidding");
                            c.saveInBackground();
                        }
                    }).show();
                } else {
//                    conversations.add(position,c);
//                    adapter.notifyItemInserted(position);
                    c.setStatus("accepted");
                    c.saveInBackground();
                    c.getTask().acceptCandidate(c);
                    Snackbar.make(rvConversations, R.string.snackbar_accepted, Snackbar.LENGTH_LONG).show();
                    listener.launchChatFragment(c);

//                    Snackbar.make(rvConversations, R.string.snackbar_accepted, Snackbar.LENGTH_LONG).setAction(R.string.snackbar_undo, new View.OnClickListener(){
//                        @Override
//                        public void onClick(View view){
//                            c.getTask().setStatus("open");
//                            c.getTask().nullAcceptedOffer();
//                            c.getTask().nullCandidate();
//                            c.getTask().saveInBackground();
//                            c.setStatus("bidding");
//                            c.saveInBackground();
//                        }
//                    }).show();
                }
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                //Available drag and drop directions
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                //Available swipe directions
                int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            //Disable or Enable drag and drop by long press
            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            //Disable or Enable swiping
            @Override
            public boolean isItemViewSwipeEnabled() {
                //return false;
                return true;
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        itemTouchHelper.attachToRecyclerView(rvConversations);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        populate();
        populateConversations();
    }

    private void populate() {
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
    }

    private void populateConversations() {
        pbConversation.setVisibility(ProgressBar.VISIBLE);
        ParseQuery<Conversation> mainConversationQuery = ParseQuery.getQuery("Conversation");
        mainConversationQuery.whereEqualTo("owner", ParseUser.getCurrentUser());
        mainConversationQuery.whereEqualTo("task", task);
        mainConversationQuery.whereNotEqualTo("status","declined");
        mainConversationQuery.include("task");
        mainConversationQuery.include("posted_by");

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
                pbConversation.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }

}
