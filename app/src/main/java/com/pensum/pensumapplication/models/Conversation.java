package com.pensum.pensumapplication.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.math.BigDecimal;

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

    public void setUnreadOwnerMessageFlag(Boolean flag) { put("unread_owner_message", flag); }
    public Boolean getUnreadOwnerMessageFlag() { return getBoolean("unread_owner_message"); }

    public void setUnreadCandidateMessageFlag(Boolean flag) { put("unread_candidate_message", flag); }
    public Boolean getUnreadCandidateMessageFlag() { return getBoolean("unread_candidate_message"); }

    public void setOffer(BigDecimal offer){ put("offer",offer); }
    public Double getOffer() { return  getDouble("offer"); }
}
