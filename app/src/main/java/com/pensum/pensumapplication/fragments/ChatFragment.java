package com.pensum.pensumapplication.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.models.Conversation;
import com.pensum.pensumapplication.models.Message;

public class ChatFragment extends Fragment {

    private EditText etChatMessage;
    private Button btSendChat;
    private ParseUser sendingUser;
    private ParseUser receivingUser;
    private Conversation conversation;

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
        return inflater.inflate(R.layout.fragment_chat, parent, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
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

    private void setupMessagePosting(View view) {
        etChatMessage = (EditText) view.findViewById(R.id.etChatMessage);
        btSendChat = (Button) view.findViewById(R.id.btSendChat);
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
                            Toast.makeText(getContext(), "Successfully created message on Parse",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("debug", "Failed to save message", e);
                        }
                    }
                });
                etChatMessage.setText(null);
            }
        });
    }
}
