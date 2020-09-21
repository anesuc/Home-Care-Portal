package com.anesu.homecareportal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class SubmitCompletedTaskActivity extends AppCompatActivity {
    private  String taskTitlteText;
    private String taskId;
    private TextView details;
    private static final int REQUEST_CODE = 10; // Code to send to child, and expect back.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_completed_task);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        details =  (TextView) findViewById(R.id.editTextTaskNotes);
        Intent intent = getIntent();
        if (intent.hasExtra("title"))
            taskTitlteText = intent.getStringExtra("title");
        else
            taskTitlteText = "Error obtaining Task data";

        if (intent.hasExtra("taskId"))
            taskId = intent.getStringExtra("taskId");
        else
            taskId = "-1";

        getSupportActionBar().setTitle("Task Completed: "+taskTitlteText);

        findViewById(R.id.saveTaskCompleted).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTaskInRemoteDB();
            }
        });
    }

    private void updateTaskInRemoteDB() {

        String requestUrl = "http://externos.io/home_care_portal/set_completed_task/";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Volley update", ""+response); //the response contains the result from the server, a json string or any other object returned by your server
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                //startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postMap = new HashMap<>();
                postMap.put("id", taskId);
                postMap.put("details", details.getText().toString());
                //..... Add as many key value pairs in the map as necessary for your request
                return postMap;
            }
        };
//make the request to your server as indicated in your request url
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);

    }
}
