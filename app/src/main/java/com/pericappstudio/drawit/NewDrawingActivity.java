package com.pericappstudio.drawit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.cloudmine.api.CMObject;
import com.cloudmine.api.SearchQuery;
import com.cloudmine.api.db.LocallySavableCMObject;
import com.cloudmine.api.rest.response.CMObjectResponse;
import com.cloudmine.api.rest.response.ObjectModificationResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric P on 5/24/2015.
 */
public class NewDrawingActivity extends Activity implements View.OnClickListener {

    private ArrayList<String> chosenFriendsIDs, chosenFriendsUsername;
    private ArrayList<String> players;
    private String userLoggedID, pictureID, player2ID, pictureName;
    private int roundNumber;

    private DrawingView drawView;
    private ImageButton currPaint, drawBtn, newBtn, saveBtn, goBackBtn;

    private float smallBrush, mediumBrush, largeBrush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawing_layout_frame);
        drawView = (DrawingView)findViewById(R.id.drawing);

        chosenFriendsIDs = getIntent().getExtras().getStringArrayList("ChosenFriends");
        userLoggedID = getIntent().getExtras().getString("UserID");
        roundNumber = getIntent().getExtras().getInt("RoundNumber");
        pictureName = getIntent().getExtras().getString("PictureName");
        chosenFriendsUsername = getIntent().getExtras().getStringArrayList("ChosenFriendsUsername");

        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
        currPaint = (ImageButton) paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        //image buttons
        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);
        newBtn = (ImageButton)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);
        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);
        goBackBtn = (ImageButton) findViewById(R.id.goback_btn);
        goBackBtn.setOnClickListener(this);
    }

    public void paintClicked(View view){
        if(view!=currPaint){
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;
        }
    }

    @Override
    public void onClick(View view) {
        //Brush Size
        if(view.getId()==R.id.draw_btn) {
            drawView.setBrushSize(drawView.getLastBrushSize());
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(smallBrush);
                    drawView.setLastBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(mediumBrush);
                    drawView.setLastBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(largeBrush);
                    drawView.setLastBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();

            //New Button
        } else if(view.getId()==R.id.new_btn) {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing (you will lose the current stroke)?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.setDidMove(false);
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });

            newDialog.show();

            //Save button
        } else if(view.getId()==R.id.save_btn) {
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Finish Turn");
            saveDialog.setMessage("Are you done with your drawing?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    drawView.setDrawingCacheEnabled(true);
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        drawView.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 100, baos);

                        byte[] convertedImage = baos.toByteArray();
                        Thread saveImageToCloud = new Thread(new SaveImageToCloud(convertedImage));
                        saveImageToCloud.run();
                        baos.close();


                    } catch(IOException e) {

                    }
                    drawView.destroyDrawingCache();
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            saveDialog.show();
        } else if(view.getId()==R.id.goback_btn) {
            AlertDialog.Builder goBack = new AlertDialog.Builder(this);
            goBack.setTitle("Go Back to Dashboard");
            goBack.setMessage("Are you sure?");
            goBack.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    Intent intent = new Intent("android.intent.action.DASHBOARD");
                    Bundle userID = new Bundle();
                    userID.putString("UserID", userLoggedID);
                    intent.putExtras(userID);
                    startActivity(intent);
                }
            });
            goBack.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            goBack.show();
        }
    }

    final class SaveImageToCloud implements Runnable {

        byte[] convertedImage;

        public SaveImageToCloud(byte[] convertedImage) {
            this.convertedImage = convertedImage;
        }

        @Override
        public void run() {
            players = chosenFriendsIDs;
            players.add(0, userLoggedID);
            player2ID = players.get(1);
            TheDrawing theDrawing = new TheDrawing(1, 2, convertedImage, players, roundNumber, roundNumber*players.size(), pictureName, chosenFriendsUsername);
            theDrawing.save(getApplicationContext(), new Response.Listener<ObjectModificationResponse>() {
                @Override
                public void onResponse(ObjectModificationResponse modificationResponse) {
                    List<String> tempList = modificationResponse.getCreatedObjectIds();
                    pictureID = tempList.get(0);

                    LocallySavableCMObject.loadObject(getApplicationContext(), pictureID, new Response.Listener<CMObjectResponse>() {
                        @Override
                        public void onResponse(CMObjectResponse response) {
                            TheDrawing sameDrawing = (TheDrawing) response.getCMObject(pictureID);
                            sameDrawing.setPictureID(pictureID);
                            sameDrawing.save(getApplicationContext(), new Response.Listener<ObjectModificationResponse>() {
                                @Override
                                public void onResponse(ObjectModificationResponse modificationResponse) {
                                    Toast.makeText(getApplicationContext(), "Picture saved and updated successfully", Toast.LENGTH_LONG).show();
                                    updateUsers.run();
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    Thread updateUsers = new Thread(new Runnable() {
        @Override
        public void run() {
            AUser.loadAllUserProfiles(getApplicationContext(), new Response.Listener<CMObjectResponse>() {
                @Override
                public void onResponse(CMObjectResponse objectResponse) {
                    for (CMObject obj : objectResponse.getObjects()) {
                        final AUser user = (AUser) obj;
                        for(int i=0; i<players.size(); i++) {
                            if(players.get(i).equals(user.getObjectId())) {
                                LocallySavableCMObject.searchObjects(getApplicationContext(), SearchQuery.filter("userID").equal(user.getObjectId()).searchQuery(),
                                        new Response.Listener<CMObjectResponse>() {
                                            @Override
                                            public void onResponse(CMObjectResponse response) {
                                                List<CMObject> tempList = response.getObjects();
                                                UserObjectIDs userObjectIDs = (UserObjectIDs) tempList.get(0);
                                                if(!(userObjectIDs.getUserID().equals(player2ID))) {
                                                    userObjectIDs.addWIPPictureOthersTurn(pictureID);
                                                }
                                                else {
                                                    userObjectIDs.addWIPPictureUserTurn(pictureID);
                                                }
                                                userObjectIDs.save(getApplicationContext(), new Response.Listener<ObjectModificationResponse>() {
                                                    @Override
                                                    public void onResponse(ObjectModificationResponse modificationResponse) {
                                                    }
                                                });
                                            }
                                        });
                            }
                        }
                    }

                }
            });
            Intent intent = new Intent("android.intent.action.DASHBOARD");
            Bundle userID = new Bundle();
            userID.putString("UserID", userLoggedID);
            intent.putExtras(userID);
            startActivity(intent);
        }
    });

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}
