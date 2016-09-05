package com.pensum.pensumapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.activities.HomeActivity;
import com.pensum.pensumapplication.models.Conversation;
import com.pensum.pensumapplication.models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by violetaria on 8/21/16.
 */
public class MessagesFragment extends GridFragment {

    private OnConversationClickedListener listener;

    public interface OnConversationClickedListener{
        void launchConversationsFragment(Task task);
        void launchChatFragment(Conversation conversation);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnConversationClickedListener) {
            listener = (OnConversationClickedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MessagesFragment.OnConversationClickedListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((HomeActivity) getActivity()).updateTitle();
    }

    public void populateTasks() {
        List<ParseQuery<Conversation>> subConversationQueries = new ArrayList<>();

        ParseQuery<Conversation> ownerQuery = ParseQuery.getQuery("Conversation");
        ownerQuery.whereEqualTo("owner", ParseUser.getCurrentUser());
        ownerQuery.whereNotEqualTo("status","declined");
        ownerQuery.whereNotEqualTo("status","completed");

        subConversationQueries.add(ownerQuery);
        ParseQuery<Conversation> candidateQuery = ParseQuery.getQuery("Conversation");
        candidateQuery.whereEqualTo("candidate", ParseUser.getCurrentUser());
        candidateQuery.whereNotEqualTo("status","declined");
        candidateQuery.whereNotEqualTo("status","completed");

        subConversationQueries.add(candidateQuery);
        ParseQuery<Conversation> mainConversationQuery = ParseQuery.getQuery("Conversation").or(subConversationQueries).include("task");

        mainConversationQuery.findInBackground(new FindCallback<Conversation>() {
            public void done(List<Conversation> conversationsFromQuery, ParseException e) {
                if (e == null) {
                    Map<String,Task> tasksFromQuery = new HashMap<>();
                    // TODO fix the query so that it actually brings back the Tasks
                    for(Conversation c: conversationsFromQuery) {
                        if(!c.getStatus().equals("declined") && !c.getTask().getStatus().equals("completed")) {
                            tasksFromQuery.put(c.getTask().getObjectId(), c.getTask());
                        }
                    }
                    addAll(new ArrayList<>(tasksFromQuery.values()));
                } else {
                    Log.e("message", "Error Loading Messages" + e);}
            }
        });
    }

    public void showDetailFragment(Task task, Conversation conversation) {
        if(task.getStatus().equals("open") && task.getPostedBy().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
            listener.launchConversationsFragment(task);
        } else {
            // go directly to messages if you are the candidate or if task is accepted
            listener.launchChatFragment(conversation);
        }
    }

    @Override
    public void onSwipeDelete(String id) {
        // Not applicable
    }
}
