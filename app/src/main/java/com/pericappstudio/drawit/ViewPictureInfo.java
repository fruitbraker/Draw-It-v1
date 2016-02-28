package com.pericappstudio.drawit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.cloudmine.api.CMApiCredentials;
import com.cloudmine.api.db.LocallySavableCMObject;
import com.cloudmine.api.rest.response.CMObjectResponse;

import java.util.ArrayList;

/**
 * Created by Eric P on 5/27/2015.
 */
public class ViewPictureInfo extends Activity {

    public static final String APP_ID = "c048ef46cab04bb4b82e97fb480cba1b";
    public static final String API_KEY = "6308fff67fe0477ba94a3a3deccb4987";

    private TextView tvCurrentTurn, tvCurrentRound, tvTotalRound, tvTotalTurn, tvPictureName;
    private ListView playerUsernamesListView;
    private ArrayList<String> playerUsernames;
    private String userLoggedID, pictureID, userUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_info_main);
        CMApiCredentials.initialize(APP_ID, API_KEY, getApplicationContext());
        userLoggedID = getIntent().getExtras().getString("UserID");
        pictureID = getIntent().getExtras().getString("PictureID");
        userUsername = getIntent().getExtras().getString("UserUsername");
        playerUsernames = getIntent().getExtras().getStringArrayList("PlayerUsername");
        playerUsernames.add(userUsername);
        tvCurrentTurn = (TextView) findViewById(R.id.textViewPictureCurrentTurnInfo);
        tvCurrentRound = (TextView) findViewById(R.id.textViewPictureCurrentRoundInfo);
        tvTotalRound = (TextView) findViewById(R.id.textViewPictureTotalRoundInfo);
        tvTotalTurn = (TextView) findViewById(R.id.textViewPictureTotalTurnsINfo);
        tvPictureName = (TextView) findViewById(R.id.textViewPictureNameInfo);
        playerUsernamesListView = (ListView) findViewById(R.id.listView);
        populateActivity();
    }

    private void populateActivity() {

        LocallySavableCMObject.loadObject(this, pictureID, new Response.Listener<CMObjectResponse>() {
            @Override
            public void onResponse(CMObjectResponse response) {
                TheDrawing picture = (TheDrawing) response.getCMObject(pictureID);

                tvCurrentTurn.setText("" + picture.getCurrentTurn());
                tvCurrentRound.setText("" + picture.getCurrentRound());
                tvTotalRound.setText("" + picture.getTotalRounds());
                tvTotalTurn.setText("" + picture.getTotalTurns());
                tvPictureName.setText(picture.getPictureName());

                PictureInfoAdapter pictureInfoAdapter = new PictureInfoAdapter(getApplicationContext(), playerUsernames);
                playerUsernamesListView.setAdapter(pictureInfoAdapter);
            }
        });
    }

    public void goBackDashboard(View view) {
        Intent intent = new Intent("android.intent.action.DASHBOARD");

        Bundle userID = new Bundle();
        userID.putString("UserID", userLoggedID);
        intent.putExtras(userID);

        Bundle userUsername = new Bundle();
        userUsername.putString("UserUsername", this.userUsername);
        intent.putExtras(userUsername);

        startActivity(intent);
    }

    public void seePicture(View view) {
        Intent intent = new Intent(getApplicationContext(), ImageViewComplete.class);

        Bundle pictureIDBundle = new Bundle();
        pictureIDBundle.putString("PictureID", pictureID);
        intent.putExtras(pictureIDBundle);

        Bundle userID = new Bundle();
        userID.putString("UserID", userLoggedID);
        intent.putExtras(userID);

        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
