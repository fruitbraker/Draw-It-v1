package com.pericappstudio.drawit;

import com.cloudmine.api.db.LocallySavableCMObject;

import java.util.ArrayList;

/**
 * Created by Eric P on 5/23/2015.
 */
public class UserObjectIDs extends LocallySavableCMObject {

    private String userID, objectName, username;
    private ArrayList<String> friendIDs;
    private ArrayList<String> picturesWIPIDUserTurn;
    private ArrayList<String> picturesDoneIDs;
    private ArrayList<String> friendUsernames;
    private ArrayList<String> picturesWIPOthersTurn;

    public UserObjectIDs() {
        super();
    }

    public UserObjectIDs(String userID, String username) {
        this.userID = userID;
        this.username = username;
        friendIDs = new ArrayList<String>();
        picturesDoneIDs = new ArrayList<String>();
        picturesWIPIDUserTurn = new ArrayList<String>();
        friendUsernames = new ArrayList<String>();
        picturesWIPOthersTurn = new ArrayList<String>();
        objectName = "UserOBjectIDs";

    }

    public ArrayList<String> getFriendUsernames() {
        return friendUsernames;
    }

    public ArrayList<String> getPicturesDoneIDs() {
        return picturesDoneIDs;
    }

    public ArrayList<String> getPicturesWIPIDUserTurn() {
        return picturesWIPIDUserTurn;
    }

    public ArrayList<String> getFriendIDs() {
        return friendIDs;
    }

    public ArrayList<String> getPicturesWIPOthersTurn() {
        return picturesWIPOthersTurn;
    }

    public String getUserID() {
        return userID;
    }

    public void addFriendID(String friendID) {
        friendIDs.add(friendID);
    }

    public void addFriendUsername(String friendUsername) {
        friendUsernames.add(friendUsername);
    }

    public void addWIPPictureUserTurn(String pictureID) {
        picturesWIPIDUserTurn.add(0, pictureID);
        picturesWIPOthersTurn.remove(pictureID);
        picturesWIPOthersTurn.remove(null);
        picturesWIPOthersTurn.remove("");
    }

    public void addWIPPictureOthersTurn(String pictureID) {
        picturesWIPIDUserTurn.remove(pictureID);
        picturesWIPIDUserTurn.remove(null);
        picturesWIPIDUserTurn.remove("");
        picturesWIPOthersTurn.add(0, pictureID);
    }

    public void addDonePicture(String pictureID) {
        picturesWIPIDUserTurn.remove(pictureID);
        picturesWIPOthersTurn.remove(pictureID);
        picturesWIPIDUserTurn.remove(null);
        picturesWIPIDUserTurn.remove("");
        picturesWIPOthersTurn.remove(null);
        picturesWIPOthersTurn.remove("");
        picturesDoneIDs.add(pictureID);
    }

    public String getUsername() {
        return username;
    }

    public String getObjectName() {
        return objectName;
    }
}
