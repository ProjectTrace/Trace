package com.uw.hcde.fizzlab.trace.navigation;

import com.parse.ParseUser;

/**
 * Created by yellowleaf on 5/18/15.
 */
public class UserAndDistance {
    private ParseUser user;
    private int distance;

    public UserAndDistance(ParseUser user, int dis) {
        this.user = user;
        this.distance = dis;
    }

    public ParseUser getUser() {
        return this.user;
    }

    public int getDis() {
        return this.distance;
    }
}
