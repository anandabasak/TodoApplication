package com.anandroidstudios.todoapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import static com.anandroidstudios.todoapplication.LoginName.Name;
import static com.anandroidstudios.todoapplication.LoginName.mypreference;


public class TaskHome extends AppCompatActivity {

    Activity activity;
    TaskDBHelper mydb;
    NoScrollListView taskListToday, taskListTomorrow, taskListUpcoming;
    NestedScrollView scrollView;
    ProgressBar loader;
    TextView todayText,tomorrowText,upcomingText ,taskLeft;
    ArrayList<HashMap<String, String>> todayList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tomorrowList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> upcomingList = new ArrayList<HashMap<String, String>>();

    public static String KEY_ID = "id";
    public static String
            KEY_TASK = "task";
    public static String KEY_DATE = "date";
    public static String KEY_TIME = "time";
    private static final String TAG = "MyActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_home);

        Get();
        activity = TaskHome.this;
        mydb = new TaskDBHelper(activity);
        scrollView = findViewById(R.id.scrollView);
        loader = findViewById(R.id.loader);
        taskListToday = findViewById(R.id.taskListToday);
        taskListTomorrow = findViewById(R.id.taskListTomorrow);
        taskListUpcoming = findViewById(R.id.taskListUpcoming);

        todayText = findViewById(R.id.todayText);
        tomorrowText = findViewById(R.id.tomorrowText);
        upcomingText = findViewById(R.id.upcomingText);
        taskLeft = findViewById(R.id.tasksremaining);
    }

    public void Get() {
        TextView name = findViewById(R.id.username);
        SharedPreferences sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains(Name)) {
            name.setText(String.format("Hey, %s!", sharedpreferences.getString(Name, "")));
        }
    }


    public void openAddTask(View v)
    {
        Intent i = new Intent(this, AddTask.class);
        startActivity(i);
    }

    public void populateData()
    {
        mydb = new TaskDBHelper(activity);
        scrollView.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
        LoadTask loadTask = new LoadTask();
        loadTask.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        populateData();
    }


    class LoadTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            todayList.clear();
            tomorrowList.clear();
            upcomingList.clear();
        }

        protected String doInBackground(String... args) {
            String xml = "";

            /* ===== TODAY ========*/
            Cursor today = mydb.getDataToday();
            loadDataList(today, todayList);
            /* ===== TODAY ========*/

            /* ===== TOMORROW ========*/
            Cursor tomorrow = mydb.getDataTomorrow();
            loadDataList(tomorrow, tomorrowList);
            /* ===== TOMORROW ========*/

            /* ===== UPCOMING ========*/
            Cursor upcoming = mydb.getDataUpcoming();
            loadDataList(upcoming, upcomingList);
            /* ===== UPCOMING ========*/

            return xml;
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        @Override
        protected void onPostExecute(String xml) {


            loadListView(taskListToday,todayList);
            loadListView(taskListTomorrow,tomorrowList);
            loadListView(taskListUpcoming,upcomingList);


            if(todayList.size()>0)
            {
                todayText.setVisibility(View.VISIBLE);

                if(todayList.size()==1){
                    taskLeft.setText(String.format("You have %d task left for today.", todayList.size()));
                }
                else{
                    taskLeft.setText(String.format("You have %d tasks left for today.", todayList.size()));
                }

            }else{
                todayText.setVisibility(View.GONE);
                taskLeft.setText("You have no tasks left for today");
            }

            if(tomorrowList.size()>0)
            {
                tomorrowText.setVisibility(View.VISIBLE);
            }else{
                tomorrowText.setVisibility(View.GONE);
            }

            if(upcomingList.size()>0)
            {
                upcomingText.setVisibility(View.VISIBLE);
            }else{
                upcomingText.setVisibility(View.GONE);
            }


            loader.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
    }



    public void loadDataList(Cursor cursor, ArrayList<HashMap<String, String>> dataList)
    {
        if(cursor!=null ) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {

                HashMap<String, String> mapToday = new HashMap<String, String>();
                mapToday.put(KEY_ID, cursor.getString(0));
                mapToday.put(KEY_TASK, cursor.getString(1));
                mapToday.put(KEY_DATE, Function.Epoch2DateString(cursor.getString(2), "dd-MM-yyyy"));
                mapToday.put(KEY_TIME , cursor.getString(3));
                dataList.add(mapToday);
                cursor.moveToNext();
            }
        }
    }


    public void loadListView(ListView listView, final ArrayList<HashMap<String, String>> dataList)
    {
        ListTaskAdapter adapter = new ListTaskAdapter(activity, dataList);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                Intent i = new Intent(activity, AddTask.class);
                i.putExtra("isUpdate", true);
                i.putExtra("id", dataList.get(+position).get(KEY_ID));
                startActivity(i);
                return false;
            }
        });
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                Intent i = new Intent(activity, AddTask.class);
                i.putExtra("isUpdate", true);
                i.putExtra("id", dataList.get(+position).get(KEY_ID));
                startActivity(i);
            }
        });*/

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String text = dataList.get(+position).get(KEY_TASK);
                Log.d(TAG, text);


            }
        });


    }
}
