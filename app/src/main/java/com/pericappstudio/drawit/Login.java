package com.pericappstudio.drawit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cloudmine.api.CMApiCredentials;
import com.cloudmine.api.CMSessionToken;
import com.cloudmine.api.CMUser;
import com.cloudmine.api.rest.response.LoginResponse;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Eric P on 5/23/2015.
 */
public class Login extends Activity {
    private EditText email, password;
    private Button loginButton, createButton;
    private CheckBox checkBox;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private CMSessionToken cmSessionToken;
    private boolean isAutoLog;

    public static final String APP_ID = "c048ef46cab04bb4b82e97fb480cba1b";
    public static final String API_KEY = "6308fff67fe0477ba94a3a3deccb4987";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        CMApiCredentials.initialize(APP_ID, API_KEY, getApplicationContext());
        email = (EditText) findViewById(R.id.editTextLoginEmail);
        password = (EditText) findViewById(R.id.editTextLoginPass);
        checkBox = (CheckBox) findViewById(R.id.checkBoxKeepLogIn);
        loginButton = (Button) findViewById(R.id.buttonLogin);
        createButton = (Button) findViewById(R.id.buttonCreateAccount);

        sharedPreferences = getSharedPreferences("LoginTest", Context.MODE_PRIVATE);
        isAutoLog = sharedPreferences.getBoolean("AutoLogin", false);

        if(isAutoLog)
            autoLog();
    }

    public void login(View view) {
        // instantiate a CMUser instance with an email and password (presumably from the UI)

        final String emailLogin = email.getText().toString().trim();
        String passwordLogin = password.getText().toString();

        if(!emailLogin.equals("") && !passwordLogin.equals("")) {

            CMUser user = new CMUser(emailLogin, passwordLogin);

            user.login(this, new Response.Listener<LoginResponse>() {
                @Override
                public void onResponse(LoginResponse loginResponseResponse) {
                    AUser testUser = loginResponseResponse.getUserObject(AUser.class);
                    Intent intent = new Intent("android.intent.action.DASHBOARD");

                    Bundle sessionTokenBundle = new Bundle();
                    String sessionToken = loginResponseResponse.getSessionToken().transportableRepresentation();
                    sessionTokenBundle.putString("SessionString", sessionToken);


                    Bundle userID = new Bundle();
                    userID.putString("UserID", testUser.getObjectId());
                    intent.putExtras(userID);

                    Bundle userUsername = new Bundle();
                    userUsername.putString("UserUsername", testUser.getUserUsername());
                    intent.putExtras(userUsername);

                    boolean shouldKeepLoggedIn = checkBox.isChecked();
                    if(shouldKeepLoggedIn) {
                        editor = sharedPreferences.edit();
                        editor.putString("UserID", testUser.getObjectId());
                        editor.putString("SessionToken", sessionToken);
                        editor.putBoolean("AutoLogin", true);
                        editor.commit();
                    }


                    startActivity(intent);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getApplicationContext(), "Email or password is invalid.", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "One or more of the fields are invalid.", Toast.LENGTH_LONG).show();
        }
    }

    private void autoLog() {
        String transportString = sharedPreferences.getString("SessionToken", null);
        if(transportString != null)
            cmSessionToken = new CMSessionToken(transportString);
        else {
            Toast.makeText(getApplicationContext(), "Auto login failed.", Toast.LENGTH_LONG).show();
            return;
        }
        Date expiredDate = cmSessionToken.getExpiredDate();
        Long expiredTime = expiredDate.getTime();
        Calendar calendar = Calendar.getInstance();
        Date currentDate = new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), (calendar.get(Calendar.DAY_OF_MONTH)));
        Long currentDateTime = currentDate.getTime();
        if(currentDateTime > expiredTime) {
            Toast.makeText(getApplicationContext(), "Logging In", Toast.LENGTH_LONG).show();
            Intent intent = new Intent("android.intent.action.DASHBOARD");

            Bundle sessionTokenBundle = new Bundle();
            String sessionToken = transportString;
            sessionTokenBundle.putString("SessionString", sessionToken);


            Bundle userID = new Bundle();
            String userIDString = sharedPreferences.getString("UserID", null);
            userID.putString("UserID", userIDString);
            if(userIDString != null) {
                intent.putExtras(userID);
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(), "Auto login failed.", Toast.LENGTH_LONG).show();
                return;
            }
        }

    }

    public void createAccount(View view) {
        Intent intent = new Intent("android.intent.action.CREATE");
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
