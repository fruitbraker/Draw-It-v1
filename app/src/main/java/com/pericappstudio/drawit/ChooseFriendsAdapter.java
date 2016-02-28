package com.pericappstudio.drawit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;

import java.util.ArrayList;

/**
 * Created by Eric P on 5/24/2015.
 */
public class ChooseFriendsAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<String> friendsUsername = new ArrayList<String>();
    private ArrayList<String> friendsIDs = new ArrayList<String>();
    private ArrayList<String> chosenFriendsID;
    private ArrayList<String> chosenFriendsUsername;
    private Context context;

    public ChooseFriendsAdapter(ArrayList<String> friendsUsername, ArrayList<String> friendsIDS, Context context) {
        this.friendsUsername = friendsUsername;
        this.friendsIDs = friendsIDS;
        chosenFriendsID = new ArrayList<String>();
        chosenFriendsUsername = new ArrayList<String>();
        this.context = context;
    }

    public ArrayList<String> getChosenFriendsID() {
        return chosenFriendsID;
    }

    public ArrayList<String> getChosenFriendsUsername() {
        return chosenFriendsUsername;
    }

    @Override
    public int getCount() {
        return friendsUsername.size();
    }

    @Override
    public Object getItem(int i) {
        return friendsUsername.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.choose_friends_listview, null);
        }

        final CheckBox chooseFriend = (CheckBox) view.findViewById(R.id.checkBoxFriend);
        chooseFriend.setText(friendsUsername.get(position));
        chooseFriend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chosenFriendsID.add(friendsIDs.get(position));
                    chosenFriendsUsername.add(friendsUsername.get(position));
                } else if (!b) {
                    chosenFriendsID.remove(friendsIDs.get(position));
                    chosenFriendsUsername.remove(friendsUsername.get(position));
                }
            }
        });

        return view;
    }
}
