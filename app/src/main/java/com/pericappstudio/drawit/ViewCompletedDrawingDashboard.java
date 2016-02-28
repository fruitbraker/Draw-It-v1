package com.pericappstudio.drawit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.cloudmine.api.CMApiCredentials;
import com.cloudmine.api.CMObject;
import com.cloudmine.api.SearchQuery;
import com.cloudmine.api.db.LocallySavableCMObject;
import com.cloudmine.api.rest.response.CMObjectResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric P on 5/25/2015.
 */
public class ViewCompletedDrawingDashboard extends Activity {
    public static final String APP_ID = "c048ef46cab04bb4b82e97fb480cba1b";
    public static final String API_KEY = "6308fff67fe0477ba94a3a3deccb4987";


    private ArrayList<String> completedDrawingNames = new ArrayList<String>();
    private ArrayList<String> completedDrawingIDs = new ArrayList<String>();
    private TextView textView;
    private String userLoggedID;
    private CompletedDrawingsAdapter completedDrawingsAdapter;
    private ListView completedDrawingListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.completed_drawings_main);
        CMApiCredentials.initialize(APP_ID, API_KEY, getApplicationContext());
        completedDrawingListview = (ListView) findViewById(R.id.listViewCompletedDrawings);
        userLoggedID = getIntent().getExtras().getString("UserID");
        textView = (TextView) findViewById(R.id.textViewCompletedDrawings);
        setAdapter.run();
    }

    Thread setAdapter = new Thread(new Runnable() {
        @Override
        public void run() {
            LocallySavableCMObject.searchObjects(getApplicationContext(), SearchQuery.filter("userID").equal(userLoggedID).searchQuery(),
                    new Response.Listener<CMObjectResponse>() {
                        @Override
                        public void onResponse(CMObjectResponse response) {
                            List<CMObject> tempList = response.getObjects();
                            UserObjectIDs user = (UserObjectIDs) tempList.get(0);
                            completedDrawingIDs = user.getPicturesDoneIDs();

                            if (completedDrawingIDs.size() > 0 && completedDrawingIDs != null) {
                                for (int i = 0; i < completedDrawingIDs.size(); i++) {
                                    completedDrawingNames.add(null);
                                }
                                LocallySavableCMObject.loadObjects(getApplicationContext(), completedDrawingIDs, new Response.Listener<CMObjectResponse>() {
                                    @Override
                                    public void onResponse(CMObjectResponse response) {
                                        List<CMObject> tempList = response.getObjects();
                                        for (int i = 0; i < tempList.size(); i++) {
                                            TheDrawing picture = (TheDrawing) tempList.get(i);
                                            int indexPosition = completedDrawingIDs.indexOf(picture.getObjectId());
                                            completedDrawingNames.add(indexPosition, picture.getPictureName());
                                        }
                                        for (int i = 0; i < completedDrawingIDs.size(); i++) {
                                            completedDrawingNames.remove(null);
                                            completedDrawingNames.remove("");
                                        }
                                        completedDrawingsAdapter = new CompletedDrawingsAdapter(completedDrawingIDs, completedDrawingNames, getApplicationContext(), userLoggedID);
                                        completedDrawingListview.setAdapter(completedDrawingsAdapter);
                                    }
                                });
                            } else {
                                textView.setText("No completed drawings found.");
                            }

                        }
                    });
        }
    });

    public void goBackToDashboard(View view) {
        Intent intent = new Intent("android.intent.action.DASHBOARD");

        Bundle userID = new Bundle();
        userID.putString("UserID", userLoggedID);
        intent.putExtras(userID);

        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
