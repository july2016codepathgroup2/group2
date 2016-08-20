package com.codepath.pensum.models;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by violetaria on 8/17/16.
 */
@ParseClassName("Task")
public class Task extends ParseObject {
    public void setType(String type) {
        put("type",type);
    }

    public void setBudget(BigDecimal budget) {
        put("budget",budget);
    }

    public void setDescription(String description) {
        put("description",description);
    }

    public void setImages(ArrayList<String> images) {
        put("images",images);
    }

    public void setLatitude(Double latitude) {
        put("latitude",latitude);
    }

    public void setLongitude(Double longitude) {
        put("longitude",longitude);
    }

    @Override
    public void setObjectId(String objectId) {
        put("object_id",objectId);
    }

    public void setPostedBy(ParseUser postedBy) {
        put("posted_by",postedBy);
    }

    public void setStatus(int status) {
        put("status", status);
    }

    public void setTitle(String title) {
        put("title",title);
    }

    public String getObjectId() { return getString("object_id"); }

    public JSONArray getImages() {
        return getJSONArray("images");
    }

    public Double getLatitude() { return getDouble("latitude"); }

    public Double getLongitude() { return getDouble("longitude"); }

    public Double getBudget() {
        return getDouble("budget");
    }

    public String getDescription() {
        return getString("description");
    }

    public ParseUser getPostedBy()  {return getParseUser("posted_by");}

    public int getStatus() {
        return getInt("status");
    }

    public String getTitle() {
        return getString("title");
    }

    public String getType() {
        return getString("type");
    }

    public LatLng getLatLng() {
        LatLng latlng = new LatLng(getLatitude(), getLongitude());
        return latlng;
    }

    public Task(){
    }

    public Task(String title, String description, ParseUser postedBy, BigDecimal budget) {
        super();
        setTitle(title);
        setDescription(description);
        setPostedBy(postedBy);
        setBudget(budget);
        setStatus(0);
    }


}
