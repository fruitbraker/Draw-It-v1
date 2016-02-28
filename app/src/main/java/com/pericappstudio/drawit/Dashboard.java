package com.pericappstudio.drawit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.cloudmine.api.rest.response.ObjectModificationResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric P on 5/24/2015.
 */
public class Dashboard extends Activity {

    public static final String APP_ID = "c048ef46cab04bb4b82e97fb480cba1b";
    public static final String API_KEY = "6308fff67fe0477ba94a3a3deccb4987";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String userLoggedID, userUsername;
    private ArrayList<String> friendUsernames;
    private ArrayList<String> pictureNames = new ArrayList<String>();
    private ArrayList<String> pictureIDs = new ArrayList<String>();
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        CMApiCredentials.initialize(APP_ID, API_KEY, getApplicationContext());

        sharedPreferences = getSharedPreferences("LoginTest", Context.MODE_PRIVATE);

        userLoggedID = getIntent().getExtras().getString("UserID");
        userUsername = getIntent().getExtras().getString("UserUsername");
        Thread setFriendUsernames = new Thread(new setFriendUsernames());
        editText = (EditText) findViewById(R.id.editTextRealAddFriend);
        setFriendUsernames.run();

    }

    public void draw(View view) {
        Intent intent = new Intent("android.intent.action.CREATENEWDRAWING");
        Bundle userID = new Bundle();
        userID.putString("UserID", userLoggedID);
        intent.putExtras(userID);
        startActivity(intent);
    }

    public void viewCompletedDrawings(View view) {
        Intent intent = new Intent("android.intent.action.VIEWCOMPLETEDASHBOARD");
        Bundle userID = new Bundle();
        userID.putString("UserID", userLoggedID);
        userID.putString("UserUsername", userUsername);
        intent.putExtras(userID);
        startActivity(intent);
    }

    final class setFriendUsernames implements Runnable {

        @Override
        public void run() {
            LocallySavableCMObject.searchObjects(getApplicationContext(), SearchQuery.filter("userID").equal(userLoggedID).searchQuery(),
                    new Response.Listener<CMObjectResponse>() {
                        @Override
                        public void onResponse(CMObjectResponse response) {
                            List<CMObject> tempList = response.getObjects();
                            UserObjectIDs user = (UserObjectIDs) tempList.get(0);
                            friendUsernames = user.getFriendUsernames();
                            pictureIDs = user.getPicturesWIPIDUserTurn();

                            FriendAdapter adapterFriend = new FriendAdapter(friendUsernames, getApplicationContext());
                            ListView listViewFriend = (ListView) findViewById(R.id.listViewFriends);
                            listViewFriend.setAdapter(adapterFriend);

                            if (pictureIDs.size() > 0 && pictureIDs != null) {
                                for (int i = 0; i < pictureIDs.size(); i++) {
                                    pictureNames.add(null);
                                }
                                LocallySavableCMObject.loadObjects(getApplicationContext(), pictureIDs, new Response.Listener<CMObjectResponse>() {
                                    @Override
                                    public void onResponse(CMObjectResponse response) {
                                        List<CMObject> tempList = response.getObjects();
                                        for (int i = 0; i < tempList.size(); i++) {
                                            TheDrawing picture = (TheDrawing) tempList.get(i);
                                            int indexPosition = pictureIDs.indexOf(picture.getObjectId());
                                            pictureNames.add(indexPosition, picture.getPictureName());
                                        }
                                        for (int i = 0; i < pictureIDs.size(); i++) {
                                            pictureNames.remove(null);
                                            pictureNames.remove("");
                                        }
                                        ContinueDrawingAdapter continueDrawingAdapter = new ContinueDrawingAdapter(pictureNames, pictureIDs, getApplicationContext(), getParent(), userLoggedID);
                                        ListView continueDrawingListView = (ListView) findViewById(R.id.listViewDrawings);
                                        continueDrawingListView.setAdapter(continueDrawingAdapter);
                                    }
                                });
                            }
                        }
                    });
        }
    }

    //Gets the username from the edit text and filters it
    public void addUser(View view) {

        AUser.loadAllUserProfiles(getApplicationContext(), new Response.Listener<CMObjectResponse>() {
            @Override
            public void onResponse(CMObjectResponse objectResponse) {
                List<CMObject> smallList = objectResponse.getObjects();
                String usernameText = editText.getText().toString().trim();
                for (CMObject obj : smallList) {
                    AUser user = (AUser) obj;
                    if (user.getUserUsername().equals(usernameText)) {
                        Toast.makeText(getApplicationContext(), "Finding User", Toast.LENGTH_LONG).show();
                        confirmAdd(user.getObjectId(), user.getUserUsername());
                    }
                }

            }
        });
    }

    //Adds the friend's ID for realsie
    private void confirmAdd(final String friendID, final String friendUsername) {
        LocallySavableCMObject.searchObjects(this, SearchQuery.filter("userID").equal(userLoggedID).searchQuery(),
                new Response.Listener<CMObjectResponse>() {
                    @Override
                    public void onResponse(CMObjectResponse response) {
                        boolean hasAdded = false;
                        List<CMObject> tempList = response.getObjects();
                        UserObjectIDs userObjectIDs = (UserObjectIDs) tempList.get(0);
                        ArrayList<String> userFriendIDs = userObjectIDs.getFriendIDs();
                        for (String addedFriendIDs : userFriendIDs) {
                            if (addedFriendIDs.equals(friendID)) {
                                hasAdded = true;
                                break;
                            }
                        }
                        if (!hasAdded) {
                            userObjectIDs.addFriendID(friendID);
                            userObjectIDs.addFriendUsername(friendUsername);
                            userObjectIDs.save(getApplicationContext(), new Response.Listener<ObjectModificationResponse>() {
                                @Override
                                public void onResponse(ObjectModificationResponse modificationResponse) {
                                    Toast.makeText(getApplicationContext(), "Friend Added!", Toast.LENGTH_LONG).show();
                                }
                            });

                            LocallySavableCMObject.searchObjects(getApplicationContext(), SearchQuery.filter("userID").equal(friendID).searchQuery(),
                                    new Response.Listener<CMObjectResponse>() {
                                        @Override
                                        public void onResponse(CMObjectResponse response) {
                                            List<CMObject> tempList = response.getObjects();
                                            UserObjectIDs userObjectIDs = (UserObjectIDs) tempList.get(0);
                                            userObjectIDs.addFriendID(userLoggedID);
                                            userObjectIDs.addFriendUsername(userUsername);
                                            userObjectIDs.save(getApplicationContext(), new Response.Listener<ObjectModificationResponse>() {
                                                @Override
                                                public void onResponse(ObjectModificationResponse modificationResponse) {
                                                    refresh(null);
                                                }
                                            });
                                        }
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), "Friend already added.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void refresh(View view) {
        Thread setFriendUsernames = new Thread(new setFriendUsernames());
        pictureIDs = new ArrayList<String>();
        pictureNames = new ArrayList<String>();
        Toast.makeText(getApplicationContext(), "Refreshing", Toast.LENGTH_LONG).show();
        setFriendUsernames.run();
    }

    public void logout(View view) {

        AUser.loadAllUserProfiles(getApplicationContext(), new Response.Listener<CMObjectResponse>() {
            @Override
            public void onResponse(CMObjectResponse objectResponse) {
                List<CMObject> smallList = objectResponse.getObjects();
                for (CMObject obj : smallList) {
                    AUser user = (AUser) obj;
                    if (user.getObjectId().equals(userLoggedID)) {
                        Toast.makeText(getApplicationContext(), "Attempting to log out", Toast.LENGTH_SHORT).show();
                        editor = sharedPreferences.edit();
                        editor.remove("UserID");
                        editor.remove("SessionToken");
                        editor.remove("AutoLogin");
                        editor.commit();
                        Intent intent = new Intent("android.intent.action.LOGIN");
                        Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                }

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
