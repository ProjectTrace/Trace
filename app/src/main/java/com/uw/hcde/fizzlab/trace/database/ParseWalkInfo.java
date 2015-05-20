package com.uw.hcde.fizzlab.trace.database;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by yellowleaf on 5/18/15.
 */
@ParseClassName("WalkInfo")
public class ParseWalkInfo extends ParseObject{
    public static final String KEY_USER = "User";
    public static final String KEY_DISTANCE = "DISTANCE";



    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setDis(int distance) {
        put(KEY_DISTANCE, distance);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public int getDis() {
        return getInt(KEY_DISTANCE);
    }
}
