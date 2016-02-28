package com.pericappstudio.drawit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.cloudmine.api.db.LocallySavableCMObject;
import com.cloudmine.api.rest.response.CMObjectResponse;

import java.util.ArrayList;

/**
 * Created by Eric P on 5/24/2015.
 */
public class ContinueDrawingAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> pictureNames = new ArrayList<String>();
    private ArrayList<String> pictureIDs = new ArrayList<String>();
    private Activity activity;

    private Context context;
    private int position;
    private String userLoggedID;

    public ContinueDrawingAdapter(ArrayList<String> pictureNames, ArrayList<String> pictureIDs, Context context, Activity activity, String userLoggedID) {
        this.pictureNames = pictureNames;
        this.pictureIDs = pictureIDs;
        this.context = context;
        this.activity = activity;
        this.userLoggedID = userLoggedID;
    }

    @Override
    public int getCount() {
        return pictureNames.size();
    }

    @Override
    public Object getItem(int i) {
        return pictureNames.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        this.position = position;
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.your_turn_listview, null);
        }

        TextView pictureNameTV = (TextView) view.findViewById(R.id.textViewPictureNameContinue);
        pictureNameTV.setText(pictureNames.get(position));

        Button continueButton = (Button) view.findViewById(R.id.buttonContinue);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(), ContinueDrawingActivity.class);

                Bundle pictureID = new Bundle();
                pictureID.putString("PictureID", pictureIDs.get(position));
                intent.putExtras(pictureID);
                Bundle userID = new Bundle();
                userID.putString("UserID", userLoggedID);
                intent.putExtras(userID);
                parent.getContext().startActivity(intent);
            }
        });

        Button showInfo = (Button) view.findViewById(R.id.buttonListViewInfo);
        showInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocallySavableCMObject.loadObject(context, pictureIDs.get(position), new Response.Listener<CMObjectResponse>() {
                    @Override
                    public void onResponse(CMObjectResponse response) {
                        TheDrawing drawing = (TheDrawing) response.getCMObject(pictureIDs.get(position));
                        ArrayList<String> picturePlayerUsername = drawing.getPlayersUsername();

                        Intent intent = new Intent("android.intent.action.PICTUREINFO");

                        Bundle bundle = new Bundle();
                        bundle.putString("PictureID", pictureIDs.get(position));
                        bundle.putString("UserID", userLoggedID);
                        bundle.putStringArrayList("PlayerUsername", picturePlayerUsername);
                        intent.putExtras(bundle);
                        parent.getContext().startActivity(intent);
                    }
                });

            }
        });
        return view;
    }
}
