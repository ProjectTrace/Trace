package com.uw.hcde.fizzlab.trace.database;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * @author tianchi
 */
@ParseClassName("Log")
public class ParseLog extends ParseObject {

    // Related table keys
    public static final String KEY_USER = "user";
    public static final String KEY_MESSAGE = "message";

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setMessage(String message) {
        put(KEY_MESSAGE, message);
    }

    public static ParseQuery<ParseLog> getQuery() {
        return ParseQuery.getQuery(ParseLog.class);
    }
}
