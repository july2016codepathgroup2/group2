package com.pensum.pensumapplication.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by violetaria on 8/21/16.
 */
@ParseClassName("Conversation")
public class Conversation extends ParseObject{
    public Conversation() {}

    public void setTask(Task task){
        put("task",task);
    }
    public Task getTask() { return (Task) getParseObject("task"); }

    public void setOwner(ParseUser owner) {
        put("owner",owner);
    }
    public ParseUser getOwner() { return getParseUser("owner"); }

    public void setCandidate(ParseUser candidate) {
        put("candidate",candidate);
    }
    public ParseUser getCandidate() { return getParseUser("candidate"); }
}
