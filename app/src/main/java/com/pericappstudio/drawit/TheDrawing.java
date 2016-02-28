package com.pericappstudio.drawit;

import com.cloudmine.api.db.LocallySavableCMObject;

import java.util.ArrayList;

/**
 * Created by Eric P on 5/24/2015.
 */
public class TheDrawing extends LocallySavableCMObject {

    private byte[] encodedImage;
    private ArrayList<String> players;
    private ArrayList<String> playersUsername;
    private int currentTurn, totalTurns, currentRound, totalRounds;
    private String pictureID, pictureName, objectName;

    public TheDrawing() {
        super();
    }

    public TheDrawing(int currentRound, int currentTurn, byte[] encodedImage, ArrayList<String> players, int totalRounds, int totalTurns, String pictureName, ArrayList<String> playersUsername) {
        this.currentRound = currentRound;
        this.currentTurn = currentTurn;
        this.encodedImage = encodedImage;
        this.players = players;
        this.totalRounds = totalRounds;
        this.totalTurns = totalTurns;
        this.pictureName = pictureName;
        this.playersUsername = playersUsername;
        objectName = "TheDrawing";
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public byte[] getEncodedImage() {
        return encodedImage;
    }

    public ArrayList<String> getPlayers() {
        return players;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public int getTotalTurns() {
        return totalTurns;
    }

    public void setEncodedImage(byte[] encodedImage) {
        this.encodedImage = encodedImage;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public String getPictureID() {
        return pictureID;
    }

    public void setPictureID(String pictureID) {
        this.pictureID = pictureID;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void finishTurn() {
        currentTurn++;
        if(currentTurn % players.size() == (players.size() - 1))
            currentRound++;
    }

    public ArrayList<String> getPlayersUsername() {
        return playersUsername;
    }

    public String getObjectName() {
        return objectName;
    }
}
