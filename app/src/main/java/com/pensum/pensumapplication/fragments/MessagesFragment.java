package com.pensum.pensumapplication.fragments;

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
//    public static MessagesFragment newInstance(int page) {
//        return (MessagesFragment) GridFragment.newInstance(page);
//    }
//    public static MessagesFragment newInstance() {
//        return (MessagesFragment) GridFragment.newInstance(0);
//    }

    public void populateTasks() {
//        ParseQuery<Conversation> innerQuery = ParseQuery.getQuery("Conversation");
//        innerQuery.whereEqualTo("owner",ParseUser.getCurrentUser());
//        ParseQuery<Task> query = ParseQuery.getQuery("Task");
//        query.whereMatchesQuery("task",innerQuery);

        List<ParseQuery<Conversation>> queries = new ArrayList<>();

        ParseQuery<Conversation> ownerQuery = ParseQuery.getQuery("Conversation");
        ownerQuery.whereEqualTo("owner", ParseUser.getCurrentUser());

        queries.add(ownerQuery);
        ParseQuery<Conversation> candidateQuery = ParseQuery.getQuery("Conversation");
        candidateQuery.whereEqualTo("candidate", ParseUser.getCurrentUser());

        queries.add(candidateQuery);
        ParseQuery<Conversation> mainConversationQuery = ParseQuery.getQuery("Conversation").or(queries);
        mainConversationQuery.findInBackground(new FindCallback<Conversation>() {
            public void done(List<Conversation> conversationsFromQuery, ParseException e) {
                if (e == null) {
                    ArrayList<Task> tasksFromQuery = new ArrayList<>();
                    // TODO fix the query so that it actually brings back the Tasks
                    for(int i = 0; i < conversationsFromQuery.size(); i++){
                        try {
                            Task task = conversationsFromQuery.get(i).getTask().fetchIfNeeded();
                            tasksFromQuery.add(task);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
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
}
