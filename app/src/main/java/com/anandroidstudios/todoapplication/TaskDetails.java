package com.anandroidstudios.todoapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class TaskDetails extends AppCompatActivity {

    TaskDBHelper mydb;
    Intent intent;
    String id;
    int color = Integer.parseInt("7e7e7e", 16) + 0xFF000000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        mydb = new TaskDBHelper(getApplicationContext());
        intent = getIntent();

        id = intent.getStringExtra("id");
        TextView task_title = findViewById(R.id.tasktitle);
        TextView task_desc = findViewById(R.id.taskdescription);
        TextView task_date = findViewById(R.id.taskdate);
        TextView task_time = findViewById(R.id.tasktime);
        Cursor task = mydb.getDataSpecific(id);
        task.moveToFirst();

        task_title.setText(task.getString(1));
        task_date.setText(Function.Epoch2DateString(task.getString(2), "dd/MM/yyyy"));
        task_time.setText(task.getString(3));
        if (task.getString(3).equals("")) {
            task_time.setText("All Day");
        }
        task_desc.setText(task.getString(4));
        if (task.getString(4).equals("")) {
            task_desc.setText("No Description Added");
            task_desc.setTextColor(color);
        }
    }
}
