package com.yardbird.justice.yardbird;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kosalgeek.genasync12.*;

import java.util.HashMap;

/**
 * Created by Justice on 03/03/2017.
 */

public class LoginTest extends AppCompatActivity {
    public static final String PREFS = "prefFile";

    EditText UsernameEt, PasswordEt;
    Button login;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_login);

        UsernameEt = (EditText) findViewById(R.id.edUsername);
        PasswordEt = (EditText) findViewById(R.id.edPassword);
        login = (Button) findViewById(R.id.btnLogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap postData = new HashMap();
                postData.put("txtUsername", UsernameEt.getText().toString());
                postData.put("txtPassword", PasswordEt.getText().toString());

                PostResponseAsyncTask loginTask = new PostResponseAsyncTask(LoginTest.this, postData, new AsyncResponse() {
                    @Override
                    public void processFinish(String s) {
                        if(s.contains("success"))
                        {
                            String uName = UsernameEt.getText().toString();

                            SharedPreferences preferences = getSharedPreferences(PREFS, 0);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("username", uName);
                            editor.commit();

                            Intent intent = new Intent(LoginTest.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                loginTask.execute("http://group8.hol.es/login.php/");
            }
        });
    }
}
