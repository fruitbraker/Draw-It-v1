package com.pericappstudio.drawit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.cloudmine.api.CMApiCredentials;
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
public class ContinueDrawingActivity extends Activity implements View.OnClickListener {
    public static final String APP_ID = "c048ef46cab04bb4b82e97fb480cba1b";
    public static final String API_KEY = "6308fff67fe0477ba94a3a3deccb4987";

    private ArrayList<String> chosenFriendsIDs;
    public ArrayList<String> players;
    private String userLoggedID, pictureID;
    private int roundNumber;

    private DrawingView drawView;
    private ImageButton currPaint, drawBtn, newBtn, saveBtn, goBackBtn;

    private byte[] encodedImage;
    private int currentTurn, playerNumber, indexPlayerMoved, indexNextPlayer, totalTurns;
    private String movedUserID, nextUserID, pictureName;

    private float smallBrush, mediumBrush, largeBrush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawing_layout_frame);
        drawView = (DrawingView)findViewById(R.id.drawing);
        CMApiCredentials.initialize(APP_ID, API_KEY, getApplicationContext());

        pictureID = getIntent().getExtras().getString("PictureID");
        userLoggedID = getIntent().getExtras().getString("UserID");
        changeBackground.run();

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
            newDialog.setTitle("Reset");
            newDialog.setMessage("Undo your stroke?");
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
            saveDialog.setMessage("Are you done wtih your turn?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    drawView.setDrawingCacheEnabled(true);
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        drawView.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 100, baos);
                        encodedImage = baos.toByteArray();
                        updatePicture.run();
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
            goBack.setMessage("Are you sure? \n lol \n");
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

    Thread updatePicture = new Thread(new Runnable(){

        @Override
        public void run() {
            LocallySavableCMObject.loadObject(getApplicationContext(), pictureID, new Response.Listener<CMObjectResponse>() {
                @Override
                public void onResponse(CMObjectResponse response) {
                    TheDrawing theDrawing = (TheDrawing) response.getCMObject(pictureID);
                    theDrawing.setEncodedImage(encodedImage);
                    players = theDrawing.getPlayers();
                    playerNumber = players.size();
                    pictureName = theDrawing.getPictureName();
                    currentTurn = theDrawing.getCurrentTurn();
                    totalTurns = theDrawing.getTotalTurns();
                    chosenFriendsIDs = theDrawing.getPlayers();
                    theDrawing.save(getApplicationContext(), new Response.Listener<ObjectModificationResponse>() {
                        @Override
                        public void onResponse(ObjectModificationResponse modificationResponse) {
                            Toast.makeText(getApplicationContext(), "Picture successfully saved!", Toast.LENGTH_LONG).show();
                            updateTurns.run();

                        }
                    });
                }
            });
        }
    });

    Thread updateTurns = new Thread(new Runnable() {
        @Override
        public void run() {
            LocallySavableCMObject.searchObjects(getApplicationContext(), SearchQuery.filter("objectName").equal("UserOBjectIDs").searchQuery(),
                    new Response.Listener<CMObjectResponse>() {
                        @Override
                        public void onResponse(CMObjectResponse response) {
                            if ((currentTurn % players.size()) == 0) {
                                indexPlayerMoved = players.size() - 1;
                                indexNextPlayer = 0;
                            } else {
                                indexPlayerMoved = (currentTurn % players.size()) - 1;
                                indexNextPlayer = (currentTurn % players.size());
                            }
                            movedUserID = players.get(indexPlayerMoved);
                            nextUserID = players.get(indexNextPlayer);

                            List<CMObject> tempList = response.getObjects();
                            for (int i = 0; i < tempList.size(); i++) {
                                UserObjectIDs userObjectIDs = (UserObjectIDs) tempList.get(i);
                                if (currentTurn == totalTurns) {
                                    for (int j = 0; j < chosenFriendsIDs.size(); j++) {
                                        if (userObjectIDs.getUserID().equals(chosenFriendsIDs.get(j))) {
                                            userObjectIDs.addDonePicture(pictureID);
                                            userObjectIDs.save(getApplicationContext(), new Response.Listener<ObjectModificationResponse>() {
                                                @Override
                                                public void onResponse(ObjectModificationResponse modificationResponse) {
                                                }
                                            });
                                        }
                                    }
                                } else if (userObjectIDs.getUserID().equals(nextUserID)) {
                                    userObjectIDs.addWIPPictureUserTurn(pictureID);
                                    userObjectIDs.save(getApplicationContext(), new Response.Listener<ObjectModificationResponse>() {
                                        @Override
                                        public void onResponse(ObjectModificationResponse modificationResponse) {
                                        }
                                    });
                                } else if (userObjectIDs.getUserID().equals(movedUserID)) {
                                    userObjectIDs.addWIPPictureOthersTurn(pictureID);
                                    userObjectIDs.save(getApplicationContext(), new Response.Listener<ObjectModificationResponse>() {
                                        @Override
                                        public void onResponse(ObjectModificationResponse modificationResponse) {
                                        }
                                    });
                                }
                            }
                        }
                    });
            LocallySavableCMObject.loadObject(getApplicationContext(), pictureID, new Response.Listener<CMObjectResponse>() {
                @Override
                public void onResponse(CMObjectResponse response) {
                    TheDrawing theDrawing = (TheDrawing) response.getCMObject(pictureID);
                    if (currentTurn < totalTurns)
                        theDrawing.finishTurn();
                    theDrawing.save(getApplicationContext(), new Response.Listener<ObjectModificationResponse>() {
                        @Override
                        public void onResponse(ObjectModificationResponse modificationResponse) {
                            Toast.makeText(getApplicationContext(), "Picture successfully saved!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent("android.intent.action.DASHBOARD");
                            Bundle userID = new Bundle();
                            userID.putString("UserID", userLoggedID);
                            intent.putExtras(userID);
                            startActivity(intent);
                        }
                    });
                }
            });
        }
    });

    Thread changeBackground = new Thread(new Runnable() {
        @Override
        public void run() {
            LocallySavableCMObject.loadObject(getApplicationContext(), pictureID, new Response.Listener<CMObjectResponse>() {
                @Override
                public void onResponse(CMObjectResponse response) {
                    TheDrawing picture = (TheDrawing) response.getCMObject(pictureID);
                    byte[] encodedImage = picture.getEncodedImage();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(encodedImage, 0, encodedImage.length);
                    drawView.setBackgroundDrawable(new BitmapDrawable(bitmap));
                }
            });
        }
    });

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
