package com.pensum.pensumapplication.helpers;

import com.parse.FindCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.pensum.pensumapplication.models.Task;

import java.util.ArrayList;
import java.util.List;

public class SearchHelper {
    
    public static void searchForTasksWith(String query, String type, ParseGeoPoint location,
                                          double budget, FindCallback<Task> finishSearchCallback) {
        List<ParseQuery<Task>> queries = new ArrayList<>();
        ParseQuery<Task> mainQuery = ParseQuery.getQuery(Task.class);
        // Quick fix for case-insensitive search, won't scale
        if (!query.isEmpty()) {
            ParseQuery<Task> titleQuery = ParseQuery.getQuery(Task.class).whereMatches("title", "(" + query + ")", "i");
            ParseQuery<Task> descriptionQuery = ParseQuery.getQuery(Task.class).whereMatches("description", "(" + query + ")", "i");
            ParseQuery<Task> typeQuery = ParseQuery.getQuery(Task.class).whereMatches("type", "(" + query + ")", "i");
            ParseQuery<Task> postedByQuery = ParseQuery.getQuery(Task.class).whereMatches("posted_by", "(" + query + ")", "i");

            queries.add(titleQuery);
            queries.add(descriptionQuery);
            queries.add(typeQuery);
            queries.add(postedByQuery);
        }

        if (!queries.isEmpty()) {
            mainQuery = ParseQuery.or(queries);
        }

        if (type != null && !type.isEmpty()) {
            mainQuery.whereMatches("type", "(" + type + ")", "i");
        }
        if (location != null) {
            mainQuery.whereNear("location", location);
        }
        if (budget > 0) {
            mainQuery.whereGreaterThanOrEqualTo("budget", budget);
        }
        mainQuery.findInBackground(finishSearchCallback);

    }
}
