package com.pensum.pensumapplication.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by violetaria on 8/17/16.
 */
@ParseClassName("Users")
public class User extends ParseObject {
    public String getObjectId(){ return getString("object_id"); }
}
