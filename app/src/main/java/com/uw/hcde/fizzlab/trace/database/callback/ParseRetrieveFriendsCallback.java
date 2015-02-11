package com.uw.hcde.fizzlab.trace.database.callback;

import com.parse.ParseUser;

import java.util.List;

/**
 * @author tianchi, shuye
 */
public interface ParseRetrieveFriendsCallback {

    /**
     * @param returnCode ParseConstant.SUCCESS / FAIL
     * @param friends empty if no friends
     */
    public void callback(int returnCode, List<ParseUser> friends);
}
