package com.pericappstudio.drawit;

import com.cloudmine.api.CMUser;

/**
 * Created by Eric P on 5/23/2015.
 */
public class AUser extends CMUser {

    public String userEmail, userUsername;

    public AUser() {
        super();
    }

    public AUser(String email, String password, String username) {
        super(email, username, password);
        this.userEmail = email;
        this.userUsername = username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
    }
}
