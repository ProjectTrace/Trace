package com.uw.hcde.fizzlab.trace.database.callback;

import com.parse.ParseUser;

/**
 * Callback function for adding friends
 *
 * @author tianchi, shuye
 */
public interface ParseAddFriendCallback {

    /**
     * @param returnCode ParseConstant.SUCCESS / FAIL
     */
    public void parseAddFriendCallback(int returnCode);
}
