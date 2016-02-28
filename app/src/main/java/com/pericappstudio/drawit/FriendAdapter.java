package com.pericappstudio.drawit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Eric P on 5/24/2015.
    */
    public class FriendAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<String> friendUsername = new ArrayList<String>();
    private Context context;

    public FriendAdapter(ArrayList<String> friendUsername, Context context) {
        this.friendUsername = friendUsername;
        this.context = context;
    }


    @Override
    public int getCount() {
        return friendUsername.size();
    }

    @Override
    public Object getItem(int i) {
        return friendUsername.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.friends_listview, null);
        }
        TextView listItemText = (TextView)view.findViewById(R.id.textViewFriendUsernameListView);
        listItemText.setText(friendUsername.get(position));
        return view;
    }
}
