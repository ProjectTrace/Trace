package com.uw.hcde.fizzlab.trace.model.parse.callback;

import com.parse.ParseUser;

import java.util.List;

/**
 * Name list to Parse user list callback
 *
 * @author tianchi
 */
public interface ParseNameToUserCallback {
    /**
     * Converts name list to user list
     * @param returnCode
     * @param users
     */
     public void nameToUserCallback(int returnCode, List<ParseUser> users);
}
