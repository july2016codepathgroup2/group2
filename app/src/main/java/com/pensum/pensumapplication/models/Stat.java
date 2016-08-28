package com.pensum.pensumapplication.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.math.BigDecimal;

/**
 * Created by violetaria on 8/28/16.
 */
@ParseClassName("Stat")
public class Stat extends ParseObject {
    public Stat() {}

    public void setRating(BigDecimal rating) { put("rating",rating); }
    public Double getRating() { return getDouble("rating"); }

    public void setTasksCompleted(int tasksCompleted) { put("tasks_completed",tasksCompleted); }
    public int getTasksCompleted() { return getInt("tasks_completed"); }

    public ParseUser getUser() { return getParseUser("user");}
    public void setUser(ParseUser user) { put("user",user); }
}
