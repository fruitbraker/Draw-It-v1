package com.pericappstudio.drawit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.cloudmine.api.CMApiCredentials;
import com.cloudmine.api.db.LocallySavableCMObject;
import com.cloudmine.api.rest.response.CMObjectResponse;

/**
 * Created by Eric P on 5/25/2015.
 */
public class ImageViewComplete extends Activity {
    public static final String APP_ID = "c048ef46cab04bb4b82e97fb480cba1b";
    public static final String API_KEY = "6308fff67fe0477ba94a3a3deccb4987";

    private String pictureName, userLoggedID, pictureID;
    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_complete);
        CMApiCredentials.initialize(APP_ID, API_KEY, getApplicationContext());
        imageView = (ImageView) findViewById(R.id.imageViewCompleteDrawing);
        textView = (TextView) findViewById(R.id.textViewCompletedDrawingName);
        userLoggedID = getIntent().getExtras().getString("UserID");
        pictureID = getIntent().getExtras().getString("PictureID");
        setPicture.run();
    }

    Thread setPicture = new Thread(new Runnable() {
        @Override
        public void run() {
            LocallySavableCMObject.loadObject(getApplicationContext(), pictureID, new Response.Listener<CMObjectResponse>() {
                @Override
                public void onResponse(CMObjectResponse response) {
                    TheDrawing picture = (TheDrawing) response.getCMObject(pictureID);
                    byte[] encodedImage = picture.getEncodedImage();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(encodedImage, 0, encodedImage.length);
                    textView.setText(picture.getPictureName());
                    imageView.setImageBitmap(bitmap);
                }
            });
        }
    });

    public void goBackToViewComplete(View view) {
        Intent intent = new Intent("android.intent.action.VIEWCOMPLETEDASHBOARD");
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
