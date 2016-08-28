package com.pensum.pensumapplication.fragments;

import android.content.Context;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.models.Conversation;
import com.pensum.pensumapplication.models.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by violetaria on 8/21/16.
 */
public class MessagesFragment extends GridFragment{

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

    public void populateTasks() {
        List<ParseQuery<Conversation>> subConversationQueries = new ArrayList<>();

        ParseQuery<Conversation> ownerQuery = ParseQuery.getQuery("Conversation");
        ownerQuery.whereEqualTo("owner", ParseUser.getCurrentUser());

        subConversationQueries.add(ownerQuery);
        ParseQuery<Conversation> candidateQuery = ParseQuery.getQuery("Conversation");
        candidateQuery.whereEqualTo("candidate", ParseUser.getCurrentUser());

        subConversationQueries.add(candidateQuery);
        ParseQuery<Conversation> mainConversationQuery = ParseQuery.getQuery("Conversation").or(subConversationQueries).include("task");

        mainConversationQuery.findInBackground(new FindCallback<Conversation>() {
            public void done(List<Conversation> conversationsFromQuery, ParseException e) {
                if (e == null) {
                    ArrayList<Task> tasksFromQuery = new ArrayList<>();
                    // TODO fix the query so that it actually brings back the Tasks
                    for(int i = 0; i < conversationsFromQuery.size(); i++){
                        Task task = conversationsFromQuery.get(i).getTask();
                        tasksFromQuery.add(conversationsFromQuery.get(i).getTask());
                    }
                    int previousContentSize = tasks.size();
                    tasks.clear();
                    adapter.notifyItemRangeRemoved(0, previousContentSize);
                    tasks.addAll(tasksFromQuery);
                    adapter.notifyItemRangeInserted(0, tasksFromQuery.size());
                } else {
                    Log.e("message", "Error Loading Messages" + e);}
            }
        });
    }

    public void showDetailFragment(Task task, Conversation conversation) {
        if(task.getPostedBy() == ParseUser.getCurrentUser()){
            listener.launchConversationsFragment(task);
        } else {
            // go directly to messages if you are the candidate
            listener.launchChatFragment(conversation);
        }
    }
}
