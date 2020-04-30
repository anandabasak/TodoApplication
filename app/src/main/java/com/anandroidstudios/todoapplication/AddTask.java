package com.anandroidstudios.todoapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AddTask extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    TaskDBHelper mydb;
    DatePickerDialog dpd;
    int startYear = 0, startMonth = 0, startDay = 0;
    String dateFinal , timeFinal;
    String nameFinal, descFinal;

    Intent intent;
    Boolean isUpdate;
    String id;
    TimePickerDialog tpd;
    int mHour , mMinutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);

        mydb = new TaskDBHelper(getApplicationContext());
        intent = getIntent();
        isUpdate = intent.getBooleanExtra("isUpdate", false);



        dateFinal = todayDateString();
        Date your_date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(your_date);
        startYear = cal.get(Calendar.YEAR);
        startMonth = cal.get(Calendar.MONTH);
        startDay = cal.get(Calendar.DAY_OF_MONTH);
        mHour = cal.get(Calendar.HOUR_OF_DAY);
        mMinutes = cal.get(Calendar.MINUTE);
        if (isUpdate) {
            init_update();
        }
    }


    public void init_update() {
        id = intent.getStringExtra("id");
        TextView toolbar_task_add_title = findViewById(R.id.toolbar_task_add_title);
        EditText task_name = findViewById(R.id.task_name);
        EditText task_desc = findViewById(R.id.task_description);
        EditText task_date = findViewById(R.id.task_date);
        EditText task_time = findViewById(R.id.task_time);
        toolbar_task_add_title.setText("Update");
        Cursor task = mydb.getDataSpecific(id);
        if (task != null) {
            task.moveToFirst();

            task_name.setText(task.getString(1));
            Calendar cal = Function.Epoch2Calender(task.getString(2));
            startYear = cal.get(Calendar.YEAR);
            startMonth = cal.get(Calendar.MONTH);
            startDay = cal.get(Calendar.DAY_OF_MONTH);
            task_date.setText(Function.Epoch2DateString(task.getString(2), "dd/MM/yyyy"));
            task_time.setText(task.getString(3));
            task_desc.setText(task.getString(4));
        }

    }

    public String todayDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd/MM/yyyy", Locale.getDefault());

        return dateFormat.toString();

    }


    public void closeAddTask(View v) {
        finish();
    }


    public void doneAddTask(View v) {
        int errorStep = 0;
        EditText task_name = findViewById(R.id.task_name);
        EditText task_date = findViewById(R.id.task_date);
        EditText task_time = findViewById(R.id.task_time);
        EditText task_desc = findViewById(R.id.task_description);
        nameFinal = task_name.getText().toString();
        dateFinal = task_date.getText().toString();
        timeFinal = task_time.getText().toString();
        descFinal = task_desc.getText().toString();

        /* Checking */
        if (nameFinal.trim().length() < 1) {
            errorStep++;
            task_name.setError("Provide a task name.");
        }

        if (dateFinal.trim().length() < 4) {
            errorStep++;
            task_date.setError("Provide a specific date");
        }



        if (errorStep == 0) {
            if (isUpdate) {
                mydb.updateContact(id, nameFinal, dateFinal, timeFinal, descFinal);
                Toast.makeText(getApplicationContext(), "Task Updated.", Toast.LENGTH_SHORT).show();
            } else {
                mydb.insertContact(nameFinal, dateFinal, timeFinal, descFinal);
                Toast.makeText(getApplicationContext(), "Task Added.", Toast.LENGTH_SHORT).show();
            }

            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("startDatepickerdialog");
        if (dpd != null) dpd.setOnDateSetListener(this);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        startYear = year;
        startMonth = monthOfYear;
        startDay = dayOfMonth;
        int monthAddOne = startMonth + 1;
        String date = (startDay < 10 ? "0" + startDay : "" + startDay) + "/" +
                (monthAddOne < 10 ? "0" + monthAddOne : "" + monthAddOne) + "/" +
                startYear;
        EditText task_date = findViewById(R.id.task_date);
        task_date.setText(date);
    }



    public void showStartDatePicker(View v) {
        dpd = DatePickerDialog.newInstance(AddTask.this, startYear, startMonth, startDay);
        dpd.setOnDateSetListener(this);
        dpd.show(getFragmentManager(), "startDatepickerdialog");
    }

    public void showStartTimePicker(View v) {
        tpd = TimePickerDialog.newInstance(AddTask.this,false);
        tpd.show(getFragmentManager(), "startTimePickerDialog");

    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        EditText task_time = findViewById(R.id.task_time);
        String time ;

        if(hourOfDay<10){
            if (minute<10){
                task_time.setText("0"+hourOfDay+":0"+minute);
            }
            else {
                task_time.setText("0"+hourOfDay+":"+minute);
            }
        }
        if(minute<10){
            if (hourOfDay<10){
                task_time.setText("0"+hourOfDay+":0"+minute);
            }
            else {
                task_time.setText(hourOfDay+":0"+minute);
            }
        }
        else{
            task_time.setText(hourOfDay+":"+minute);
        }
    }
}