package com.uw.hcde.fizzlab.trace.database.callback;

import com.parse.ParseUser;

/**
 * Callback interface for retrieving friend given user name
 *
 * @author tianchi
 */
public interface ParseNameToUserCallback {

    public void nameToUserCallback(int returnCode, ParseUser user);
}
