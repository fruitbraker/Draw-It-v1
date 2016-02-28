package com.pericappstudio.drawit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.cloudmine.api.CMApiCredentials;
import com.cloudmine.api.CMObject;
import com.cloudmine.api.SearchQuery;
import com.cloudmine.api.db.LocallySavableCMObject;
import com.cloudmine.api.rest.response.CMObjectResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric P on 5/24/2015.
 */
public class CreateNewDrawingDashboard extends Activity {

    public static final String APP_ID = "c048ef46cab04bb4b82e97fb480cba1b";
    public static final String API_KEY = "6308fff67fe0477ba94a3a3deccb4987";
    private String userLoggedID, loggedUserUsername;

    private EditText numberOfTurns, pictureName;
    private ListView chooseFriends;
    private ChooseFriendsAdapter adapter;
    private ArrayList<String> friendUsernames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_drawing_dashboard);
        CMApiCredentials.initialize(APP_ID, API_KEY, getApplicationContext());
        userLoggedID = getIntent().getExtras().getString("UserID");
        setFriendsUsername.run();
        init();
    }

    private void init() {
        numberOfTurns = (EditText) findViewById(R.id.editTextNumberOfTurns);
        chooseFriends = (ListView) findViewById(R.id.listViewChooseFriends);
        pictureName = (EditText) findViewById(R.id.editTextPictureName);
    }

    public void startCreateNew(View view) {
        Intent intent = new Intent("android.intent.action.DRAWNEW");

        Bundle userID = new Bundle();
        userID.putString("UserID", userLoggedID);
        intent.putExtras(userID);

        Bundle chosenFriendsID = new Bundle();
        chosenFriendsID.putStringArrayList("ChosenFriends", adapter.getChosenFriendsID());
        intent.putExtras(chosenFriendsID);

        Bundle pictureName = new Bundle();
        pictureName.putString("PictureName", this.pictureName.getText().toString());
        intent.putExtras(pictureName);

        Bundle chosenFriendsUsername = new Bundle();
        chosenFriendsUsername.putStringArrayList("ChosenFriendsUsername", adapter.getChosenFriendsUsername());
        intent.putExtras(chosenFriendsUsername);

        Bundle roundNumber = new Bundle();
        roundNumber.putInt("RoundNumber", Integer.parseInt(numberOfTurns.getText().toString()));
        intent.putExtras(roundNumber);

        if(Integer.parseInt(numberOfTurns.getText().toString()) > 50) {
            Toast.makeText(getApplicationContext(), "Number of rounds too big", Toast.LENGTH_LONG).show();
        } else if(this.pictureName.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Invalid picture name.", Toast.LENGTH_LONG).show();
        } else if(Integer.parseInt(numberOfTurns.getText().toString()) < 0 || Integer.parseInt(numberOfTurns.getText().toString()) == 0) {
            Toast.makeText(getApplicationContext(), "Invalid round number.", Toast.LENGTH_LONG).show();
        } else if(adapter.getChosenFriendsUsername().size() == 0){
            Toast.makeText(getApplicationContext(), "You can't play by yourself.", Toast.LENGTH_LONG).show();
        } else {
            startActivity(intent);
        }

    }

    Thread setFriendsUsername = new Thread(new Runnable() {
        @Override
        public void run() {
            LocallySavableCMObject.searchObjects(getApplicationContext(), SearchQuery.filter("userID").equal(userLoggedID).searchQuery(),
                    new Response.Listener<CMObjectResponse>() {
                        @Override
                        public void onResponse(CMObjectResponse response) {
                            List<CMObject> tempList = response.getObjects();
                            UserObjectIDs user = (UserObjectIDs) tempList.get(0);
                            loggedUserUsername = user.getUsername();
                            friendUsernames = user.getFriendUsernames();
                            adapter = new ChooseFriendsAdapter(friendUsernames, user.getFriendIDs(),getApplicationContext());
                            ListView listView = (ListView) findViewById(R.id.listViewChooseFriends);
                            listView.setAdapter(adapter);
                        }
                    });
        }
    });
}
