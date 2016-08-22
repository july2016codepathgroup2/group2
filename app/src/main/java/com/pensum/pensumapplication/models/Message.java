package com.pensum.pensumapplication.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by violetaria on 8/21/16.
 */
@ParseClassName("Message")
public class Message extends ParseObject{
    public Message() {}

    public void setFrom(ParseUser from){
        put("from",from);
    }
    public void setTo(ParseUser to){
        put("to",to);
    }
    public void setMessage(String message){
        put("message",message);
    }
    public void setConversation(Conversation conversation) { put("conversation",conversation); }

    public ParseUser getFrom() { return getParseUser("from"); }
    public ParseUser getTo() { return getParseUser("to"); }
    public String getMessage() { return getString("message"); }
    public Conversation getConversation() { return (Conversation) getParseObject("conversation");}

}
