package com.uw.hcde.fizzlab.trace.database;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by yellowleaf on 5/18/15.
 */
@ParseClassName("WalkInfo")
public class ParseWalkInfo extends ParseObject{
    public static final String KEY_USER = "User";
    public static final String KEY_DISTANCE = "Distance";
    public static final String KEY_DRAWING = "Drawing";


    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setDis(int distance) {
        put(KEY_DISTANCE, distance);
    }

    public void setDrawing(ParseDrawing drawing) {
        put(KEY_DRAWING, drawing);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public int getDis() {
        return getInt(KEY_DISTANCE);
    }

    public ParseDrawing getDrawing() {
        return (ParseDrawing) getParseObject(KEY_DRAWING);
    }

    public static ParseQuery<ParseWalkInfo> getQuery() {
        return ParseQuery.getQuery(ParseWalkInfo.class);
    }
}
