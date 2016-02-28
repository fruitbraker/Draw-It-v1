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
 * Created by Eric P on 5/27/2015.
 */
public class PictureInfoAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<String> playerUsername;
    private Context context;

    public PictureInfoAdapter(Context context, ArrayList<String> playerUsername) {
        this.context = context;
        this.playerUsername = playerUsername;
    }

    @Override
    public int getCount() {
        return playerUsername.size();
    }

    @Override
    public Object getItem(int i) {
        return playerUsername.get(i);
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
            view = inflater.inflate(R.layout.picture_info_listview, null);
        }

        TextView playerName = (TextView) view.findViewById(R.id.textViewPictureFriendUsernameInfo);
        playerName.setText(playerUsername.get(position));

        return view;
    }
}
