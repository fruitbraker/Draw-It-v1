package com.pericappstudio.drawit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.cloudmine.api.CMApiCredentials;
import com.cloudmine.api.CMObject;
import com.cloudmine.api.db.LocallySavableCMObject;
import com.cloudmine.api.rest.response.CMObjectResponse;
import com.cloudmine.api.rest.response.ObjectModificationResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends Activity {

    private TextView one, two;
    private EditText editText;

    private String userLoggedID;

    private AUser loggedUser;

    List<String> aList;
    List<String> aList1;
    ArrayList<String> someCollection = new ArrayList<>();

    Car tempCar;

    public static final String APP_ID = "c048ef46cab04bb4b82e97fb480cba1b";
    public static final String API_KEY = "6308fff67fe0477ba94a3a3deccb4987";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CMApiCredentials.initialize(APP_ID, API_KEY, getApplicationContext());

        userLoggedID = getIntent().getExtras().getString("UserID");
        findLoggedUser.run();

        one = (TextView) findViewById(R.id.textView);
        two = (TextView) findViewById(R.id.textView2);
        editText = (EditText) findViewById(R.id.editTextAddUser);

        aList = new ArrayList<String>();
        aList.add("lol");
        aList.add("foo");

        aList1 = new ArrayList<String>();
        aList1.add("boo");
        aList1.add("dfjasdfljasdlkfjasdf");

    }

    public void create(View view) {
        Car honda = new Car("RAWR", 2015, aList);
        Car toyota = new Car("CRAP", 1999, aList1);


        LocallySavableCMObject.saveObjects(MainActivity.this, Arrays.asList(honda, toyota), new Response.Listener<ObjectModificationResponse>() {
            @Override
            public void onResponse(ObjectModificationResponse objectModificationResponse) {
                List<String> tempList = objectModificationResponse.getCreatedObjectIds();
                someCollection.add(tempList.get(0));
                someCollection.add(tempList.get(1));
                one.setText(tempList.get(0));
                two.setText(tempList.get(1));
            }
        });
    }

    public void updateObject(View view) {
        LocallySavableCMObject.loadObjects(this, someCollection, new Response.Listener<CMObjectResponse>() {
            @Override
            public void onResponse(CMObjectResponse response) {
                List<CMObject> coolList = response.getObjects();
                tempCar = (Car) coolList.get(0);
                updateObjectV2();
                Car tempCar = (Car) coolList.get(1);
                tempCar.setName("BWAHDFJSAFEWFadsf");
                tempCar.addRandomString("PFFFFFFFFFFFFFFFFFFT");
                tempCar.save(getApplicationContext(), new Response.Listener<ObjectModificationResponse>() {
                    @Override
                    public void onResponse(ObjectModificationResponse modificationResponse) {

                    }
                });
                two.setText(coolList.get(1).getObjectId());
            }
        });
    }

    public void updateObjectV2() {
        tempCar.setName("HAHAHAHAHAH");
        tempCar.addRandomString("MuAHAHAHAHAHAHAHHAA");
        tempCar.save(getApplicationContext(), new Response.Listener<ObjectModificationResponse>() {
            @Override
            public void onResponse(ObjectModificationResponse modificationResponse) {

            }
        });
        one.setText(tempCar.getObjectId());
    }

    //TODO getusername thingy. WORKS!!!!!!!!!!!!!!1 (For FIND MY SELF button)
    public void find(View view) {
        final String username = "fruitbraker";
        AUser.loadAllUserProfiles(getApplicationContext(), new Response.Listener<CMObjectResponse>() {
            @Override
            public void onResponse(CMObjectResponse objectResponse) {
                List<CMObject> smallList = objectResponse.getObjects();
                for (CMObject obj : smallList) {
                    AUser user = (AUser) obj;
                    if (user.getUserUsername().equals(username))
                        Toast.makeText(getApplicationContext(), user.getObjectId(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }

//    //Gets the username from the edit text and filters it
//    public void addUser(View view) {
//
//        AUser.loadAllUserProfiles(getApplicationContext(), new Response.Listener<CMObjectResponse>() {
//            @Override
//            public void onResponse(CMObjectResponse objectResponse) {
//                List<CMObject> smallList = objectResponse.getObjects();
//                String usernameText = editText.getText().toString();
//                for (CMObject obj : smallList) {
//                    AUser user = (AUser) obj;
//                    if (user.getUserUsername().equals(usernameText)) {
//                        Toast.makeText(getApplicationContext(), "HURRRRRR", Toast.LENGTH_LONG).show();
//                        confirmAdd(user.getObjectId(), user.getUserUsername());
//                    }
//                }
//
//            }
//        });
//    }
//
//    //Adds the friend's ID for realsie
//    private void confirmAdd(final String friendID, final String friendUsername) {
//        LocallySavableCMObject.searchObjects(this, SearchQuery.filter("userID").equal(loggedUser.getObjectId()).searchQuery(),
//                new Response.Listener<CMObjectResponse>() {
//                    @Override
//                    public void onResponse(CMObjectResponse response) {
//                        List<CMObject> tempList = response.getObjects();
//                        UserObjectIDs userObjectIDs = (UserObjectIDs) tempList.get(0);
//                        userObjectIDs.addFriendID(friendID);
//                        userObjectIDs.addFriendUsername(friendUsername);
//                        userObjectIDs.save(getApplicationContext(), new Response.Listener<ObjectModificationResponse>() {
//                            @Override
//                            public void onResponse(ObjectModificationResponse modificationResponse) {
//                                Toast.makeText(getApplicationContext(), "Friend Added!", Toast.LENGTH_LONG).show();
//                            }
//                        });
//                    }
//                });
//    }

    Thread findLoggedUser = new Thread(new Runnable() {
        @Override
        public void run() {
            AUser.loadAllUserProfiles(getApplicationContext(), new Response.Listener<CMObjectResponse>() {
                @Override
                public void onResponse(CMObjectResponse objectResponse) {
                    List<CMObject> smallList = objectResponse.getObjects();
                    for (CMObject obj : smallList) {
                        AUser user = (AUser) obj;
                        if (user.getObjectId().equals(userLoggedID)) {
                            loggedUser = user;
                            break;
                        }
                    }

                }
            });
        }
    });

    public void goToDashboard(View view) {
        Intent intent = new Intent("android.intent.action.DASHBOARD");
        Bundle userID = new Bundle();
        userID.putString("UserID", userLoggedID);
        intent.putExtras(userID);
        startActivity(intent);
    }
}
