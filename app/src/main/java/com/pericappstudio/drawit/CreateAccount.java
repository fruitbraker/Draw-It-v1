package com.pericappstudio.drawit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.cloudmine.api.CMApiCredentials;
import com.cloudmine.api.CMObject;
import com.cloudmine.api.CMUser;
import com.cloudmine.api.SearchQuery;
import com.cloudmine.api.rest.response.CMObjectResponse;
import com.cloudmine.api.rest.response.CreationResponse;
import com.cloudmine.api.rest.response.ObjectModificationResponse;

import java.util.List;

/**
 * Created by Eric P on 5/23/2015.
 */
public class CreateAccount extends Activity {
    public static final String APP_ID = "c048ef46cab04bb4b82e97fb480cba1b";
    public static final String API_KEY = "6308fff67fe0477ba94a3a3deccb4987";

    private EditText email, username, password, confirmPassword;
    private Button creatAccountButton, goBackButton;
    private static String emailText, usernameText, passwordText;
    private boolean emailUnique, usernameUnique, passwordCorrect, canCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);
        CMApiCredentials.initialize(APP_ID, API_KEY, getApplicationContext());
        init();
    }

    private void init() {
        email = (EditText) findViewById(R.id.editTextEmailCreate);
        username = (EditText) findViewById(R.id.editTextUsernameCreate);
        password = (EditText) findViewById(R.id.editTextPasswordCreate);
        confirmPassword = (EditText) findViewById(R.id.editTextPasswordCreateConfirm);
        creatAccountButton = (Button) findViewById(R.id.buttonCreateAccountReal);
        goBackButton = (Button) findViewById(R.id.buttonGoBackCreate);
        emailUnique = usernameUnique = passwordCorrect = canCreate = false;
    }

    public void createAccount(View view) {
        emailText = email.getText().toString().trim();
        usernameText = username.getText().toString().trim();
        passwordText = password.getText().toString();

        Thread checkEmail = new Thread(new CheckEmail(emailText));
        Thread checkUsername = new Thread(new CheckUsername(usernameText));
        Thread checkPassword = new Thread(new CheckPassword());
        Thread createAccount = new Thread(new CreateAccountConfirm());
        Handler createAccountHandler = new Handler();

        createAccountHandler.postDelayed(createAccount, 1500);
        checkEmail.run();
        checkUsername.run();
        checkPassword.run();

    }

    final class CreateAccountConfirm implements Runnable {

        @Override
        public void run() {
            if (emailUnique && usernameUnique && passwordCorrect) {
                AUser user = new AUser(emailText, passwordText, usernameText);
                user.create(getApplicationContext(), new Response.Listener<CreationResponse>() {
                    @Override
                    public void onResponse(CreationResponse creationResponse) {
                        UserObjectIDs newUserObjectIDs = new UserObjectIDs(creationResponse.getObjectId(), usernameText);
                        newUserObjectIDs.save(getApplicationContext(), new Response.Listener<ObjectModificationResponse>() {
                            @Override
                            public void onResponse(ObjectModificationResponse modificationResponse) {
                                Toast.makeText(getApplicationContext(), "Account Created!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent("android.intent.action.LOGIN");
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        }
    }


    final class CheckEmail implements Runnable {

        String emailText;

        CheckEmail(String emailText) {
            this.emailText = emailText;
        }

        @Override
        public void run() {
            CMUser.searchUserProfiles(getApplicationContext(), SearchQuery.filter("userEmail").equal(emailText).searchQuery(), new Response.Listener<CMObjectResponse>() {

                @Override
                public void onResponse(CMObjectResponse cmObjectResponse) {
                    List<CMObject> users = cmObjectResponse.getObjects();

                    if (users.size() > 0) {
                        Toast.makeText(getApplicationContext(), "Email in use mate.", Toast.LENGTH_LONG).show();
                        emailUnique = false;
                    } else {
                        emailUnique = true;
                    }
                }
            });
        }
    }


    final class CheckUsername implements Runnable {

        String usernameText;

        CheckUsername(String usernameText) {
            this.usernameText = usernameText;
        }

        @Override
        public void run() {
            CMUser.searchUserProfiles(getApplicationContext(), SearchQuery.filter("userUsername").equal(usernameText).searchQuery(), new Response.Listener<CMObjectResponse>() {

                @Override
                public void onResponse(CMObjectResponse cmObjectResponse) {
                    List<CMObject> users = cmObjectResponse.getObjects();

                    if (users.size() > 0) {
                        Toast.makeText(getApplicationContext(), "Username already taken.", Toast.LENGTH_LONG).show();
                        usernameUnique = false;
                    } else {
                        usernameUnique = true;
                    }
                }
            });
        }
    }

    final class CheckPassword implements Runnable {

        @Override
        public void run() {
            if(password.getText().toString().equals("") || confirmPassword.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "One or more of the password field is empty", Toast.LENGTH_LONG).show();
                passwordCorrect = false;
            } else if(password.getText().toString().equals(confirmPassword.getText().toString())) {
                passwordCorrect = true;
            } else {
                if(emailUnique && usernameUnique)
                    Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_LONG).show();
                passwordCorrect = false;
            }
        }
    }

    public void goBack(View view) {
        Intent intent = new Intent("android.intent.action.LOGIN");
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
