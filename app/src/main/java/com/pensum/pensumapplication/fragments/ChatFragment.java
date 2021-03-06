package com.pensum.pensumapplication.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.activities.HomeActivity;
import com.pensum.pensumapplication.adapters.ChatListAdapter;
import com.pensum.pensumapplication.models.Conversation;
import com.pensum.pensumapplication.models.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChatFragment extends Fragment {
    private final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
    private final int POLL_INTERVAL = 1000;

    @BindView(R.id.rvChat) RecyclerView rvChat;
    @BindView(R.id.etChatMessage) EditText etChatMessage;
    @BindView(R.id.btSendChat) ImageView btSendChat;
    @BindView(R.id.pbLoading) ProgressBar pb;
    private List<Message> messages;
    private ChatListAdapter adapter;
    private boolean firstLoad;
    private ParseUser sendingUser;
    private ParseUser receivingUser;
    private Conversation conversation;

    private Unbinder unbinder;
    private Handler handler;
    private Runnable refreshMessagesRunnable;

    public ChatFragment() {

    }

    public static ChatFragment newInstance(String conversationId) {
        ChatFragment chatFragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString("conversationId", conversationId);
        chatFragment.setArguments(args);
        return chatFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, parent, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        handler = new Handler();
        refreshMessagesRunnable = new Runnable() {
            @Override
            public void run() {
                refreshMessages();
                handler.postDelayed(this, POLL_INTERVAL);
            }
        };
        handler.postDelayed(refreshMessagesRunnable, POLL_INTERVAL);

        String conversationId = getArguments().getString("conversationId");
        ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.getInBackground(conversationId, new GetCallback<Conversation>() {
            @Override
            public void done(Conversation conversationFromQuery, ParseException e) {
                if (e == null) {
                    conversation = conversationFromQuery;
                    getUsersFrom(conversation);
                    setupMessagePosting(view);
                }
            }

            private void getUsersFrom(Conversation conversation) {
                sendingUser = ParseUser.getCurrentUser();
                if (conversation.getCandidate().getObjectId().equals(sendingUser.getObjectId())) {
                    receivingUser = conversation.getOwner();
                } else {
                    receivingUser = conversation.getCandidate();
                }
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((HomeActivity) getActivity()).updateTitle();
    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacks(refreshMessagesRunnable);
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setupMessagePosting(View view) {
        messages = new ArrayList<>();
        firstLoad = true;
        adapter = new ChatListAdapter(getContext(), messages);

        rvChat.setAdapter(adapter);
        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));

        btSendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = etChatMessage.getText().toString();
                Message message = new Message();
                message.setFrom(sendingUser);
                message.setTo(receivingUser);
                message.setMessage(data);
                message.setConversation(conversation);
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            refreshMessages();
                        } else {
                            Log.d("debug", "Failed to save message", e);
                        }
                    }
                });
                etChatMessage.setText(null);
            }
        });
    }

    private void refreshMessages() {
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        query.orderByDescending("createdAt");
        query.whereEqualTo("conversation", conversation);

        if(firstLoad)
            pb.setVisibility(ProgressBar.VISIBLE);

        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> queriedMessages, ParseException e) {
                if (e == null) {
                    int previousMessagesSize = messages.size();
                    // check to see that we only refresh adapter if we get new messages
                    if (previousMessagesSize != queriedMessages.size()) {
                        messages.clear();
                        adapter.notifyItemRangeRemoved(0, previousMessagesSize);
                        Collections.reverse(queriedMessages);
                        messages.addAll(queriedMessages);
                        adapter.notifyItemRangeInserted(0, queriedMessages.size());
                        rvChat.scrollToPosition(adapter.getItemCount() - 1);
                    }
                    if (firstLoad) {
                        rvChat.scrollToPosition(adapter.getItemCount() - 1);
                        firstLoad = false;

                        pb.setVisibility(ProgressBar.INVISIBLE);
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }
}
