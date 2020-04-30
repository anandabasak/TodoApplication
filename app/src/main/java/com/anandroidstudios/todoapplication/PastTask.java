package com.anandroidstudios.todoapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;


public class PastTask extends AppCompatActivity {

    private static final String TAG = "MyActivity";
    public static String KEY_ID = "id";
    public static String
            KEY_TASK = "task";
    public static String
            KEY_DESC = "desc";
    public static String KEY_DATE = "date";
    public static String KEY_TIME = "time";
    TaskDBHelper mydb;
    Intent intent;
    String id;
    NestedScrollView scrollView;
    Activity activity;
    ProgressBar loader;
    NoScrollListView taskListHistory;
    ArrayList<HashMap<String, String>> PastList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_task);
        activity = PastTask.this;
        mydb = new TaskDBHelper(activity);
        intent = getIntent();
        scrollView = findViewById(R.id.scrollViewPast);
        loader = findViewById(R.id.loaderpast);
        taskListHistory = findViewById(R.id.taskListHistory);
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateData();
    }

    public void populateData() {
        mydb = new TaskDBHelper(activity);
        scrollView.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
        LoadPastTask loadPastTask = new LoadPastTask();
        loadPastTask.execute();
    }

    public void loadDataList(Cursor cursor, ArrayList<HashMap<String, String>> dataList) {
        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {

                HashMap<String, String> mapToday = new HashMap<>();
                mapToday.put(KEY_ID, cursor.getString(0));
                mapToday.put(KEY_TASK, cursor.getString(1));
                mapToday.put(KEY_DATE, Function.Epoch2DateString(cursor.getString(2), "dd-MM-yyyy"));
                mapToday.put(KEY_TIME, cursor.getString(3));
                mapToday.put(KEY_DESC, cursor.getString(4));
                dataList.add(mapToday);
                cursor.moveToNext();
            }
        }
    }

    public void loadListView(ListView listView, final ArrayList<HashMap<String, String>> dataList) {
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Log.d(TAG, text);
                Intent i = new Intent(activity, TaskDetails.class);
                i.putExtra("id", dataList.get(+position).get(KEY_ID));
                startActivity(i);
            }
        });
    }

    class LoadPastTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PastList.clear();
        }

        @Override
        protected String doInBackground(String... strings) {
            String xml = "";
            Cursor past = mydb.getDataPast();
            loadDataList(past, PastList);
            return xml;
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        @Override
        protected void onPostExecute(String xml) {
            loadListView(taskListHistory, PastList);
            loader.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
    }


}

