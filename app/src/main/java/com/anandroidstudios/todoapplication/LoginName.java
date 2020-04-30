package com.anandroidstudios.todoapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginName extends AppCompatActivity {

    public static final String mypreference = "mypref";
    SharedPreferences sharedpreferences;
    public static final String Name = "nameKey";

    EditText username ;
    Button signup ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_input);
         username = findViewById(R.id.user_name);
         signup = findViewById(R.id.signup);

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        if(sharedpreferences.contains(Name)){
            startActivity(new Intent(LoginName.this,TaskHome.class));
            finish();   //finish current activity
        }

    }

    public void openHomePage(View v) {
        Save();
        Intent i = new Intent(this, TaskHome.class);
        startActivity(i);
    }

    public void Save() {


        String n = username.getText().toString();

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Name, n);

        editor.apply();
    }


}
