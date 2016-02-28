package com.pericappstudio.drawit;

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

import java.util.ArrayList;

/**
 * Created by Eric P on 5/25/2015.
 */
public class CompletedDrawingsAdapter extends BaseAdapter implements ListAdapter {

    private String userLoggedID;
    private Context context;
    private ArrayList<String> completedDrawingNames;
    private ArrayList<String> completedDrawingIDs;
    private int position;

    public CompletedDrawingsAdapter(ArrayList<String> completedDrawingIDs, ArrayList<String> completedDrawingNames, Context context, String userLoggedID) {
        this.completedDrawingIDs = completedDrawingIDs;
        this.completedDrawingNames = completedDrawingNames;
        this.context = context;
        this.userLoggedID = userLoggedID;
    }

    @Override
    public int getCount() {
        return completedDrawingNames.size();
    }

    @Override
    public Object getItem(int i) {
        return completedDrawingNames.get(i);
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
            view = inflater.inflate(R.layout.completed_drawings_listview, null);
        }

        TextView pictureNameTV = (TextView) view.findViewById(R.id.textViewPictureNameComplete);
        pictureNameTV.setText(completedDrawingNames.get(position));

        Button continueButton = (Button) view.findViewById(R.id.buttonComplete);
        continueButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(), ImageViewComplete.class);

                Bundle pictureID = new Bundle();
                pictureID.putString("PictureID", completedDrawingIDs.get(position));
                intent.putExtras(pictureID);
                Bundle userID = new Bundle();
                userID.putString("UserID", userLoggedID);
                intent.putExtras(userID);
                parent.getContext().startActivity(intent);
            }
        });

        return view;
    }
}
