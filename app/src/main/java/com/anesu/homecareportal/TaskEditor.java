package com.anesu.homecareportal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskEditor extends AppCompatActivity {
   // String[] languages = { "C","C++","Java","C#","PHP","JavaScript","jQuery","AJAX","JSON" };
    private TextView taskName;
    private TextView editTextDescription;
    private AutoCompleteTextView employee_name_textview;
    private AutoCompleteTextView locationInput;
    private TextView timeInput;

    private String title;
    private String description;
    private String time;
    private String assignedEmployeeId;
    private String customerId;
    private String locationId;
    private ArrayList<String> allLocationsFull;
    private ArrayList<String> allLocationsids;
    private static final int REQUEST_CODE = 10; // Code to send to child, and expect back.



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_editor);

        /*ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        taskName = (TextView) findViewById(R.id.editTextTaskName);
        employee_name_textview = (AutoCompleteTextView) findViewById(R.id.employee_name_textview);
        editTextDescription = (TextView) findViewById(R.id.editTextDescription);
        locationInput = (AutoCompleteTextView) findViewById(R.id.location_textview2);
        //timeInput = (TextView) findViewById(R.id.editTextDescription);
        allLocationsFull = new ArrayList<String>();
        allLocationsids = new ArrayList<String>();

        taskName.requestFocus();

        getLocations();

        findViewById(R.id.saveTask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = taskName.getText().toString();;
                assignedEmployeeId = "1";//employee_name_textview.getText().toString();;
                description = editTextDescription.getText().toString();
                String locationSelected = locationInput.getText().toString();
                locationId = "-1";
                boolean foundLocationId = false;
                for (int i = 0; i < allLocationsFull.size(); i++) {
                    if (allLocationsFull.get(i).equals(locationSelected)) {
                        foundLocationId = true;
                        locationId = allLocationsids.get(i);

                    }
                }
                Log.e("foundLocationId: ", String.valueOf(foundLocationId));

                if (foundLocationId)
                    addTaskToRemoteDB();
                else {
                    Snackbar.make(view, "Please enter a valid location.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void getLocations() {
        final RequestQueue queueB = Volley.newRequestQueue(getApplicationContext());
        String url = "http://externos.io/home_care_portal/get_locations/";
        //Toast.makeText(getActivity().getApplicationContext(), "Task to Add", Toast.LENGTH_LONG).show();
// Request a string response from the provided URL.
        final StringRequest stringRequestB = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                           // Log.e("response here: ",response);
                            // create the json array from String rules
                            JSONArray jsonRules = new JSONArray(response);
                            // iterate over the rules

                            String[] locations = new String[jsonRules.length()];
                            for (int i = 0; i < jsonRules.length(); i++) {
                                JSONObject obj = (JSONObject) jsonRules.get(i);
                                //System.out.println("====obj===="+obj);
                                //String name = obj.getString("name");
                                locations[i] = "["+obj.getString("id")+"] "+obj.getString("name");
                                allLocationsFull.add(locations[i]);
                                allLocationsids.add(obj.getString("id"));

                                //Log.e("suggetions: ",locations[i]);
                            }
                            //Create Array Adapter
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.select_dialog_singlechoice, locations);
                            //Find TextView control
                            //AutoCompleteTextView acTextView = (AutoCompleteTextView) findViewById(R.id.employee_name_textview);
                            //Set the number of characters the user must type before the drop down list is shown
                            locationInput.setThreshold(1);
                            //Set the adapter
                            locationInput.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //loadList();
                        // Display the first 500 characters of the response string.
                        //textView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        });
        // Add the request to the RequestQueue.
        queueB.add(stringRequestB);
    }


    private void addTaskToRemoteDB() {

        String requestUrl = "http://externos.io/home_care_portal/add_new_task/";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Volley Resultx", ""+response); //the response contains the result from the server, a json string or any other object returned by your server
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivityForResult(intent, REQUEST_CODE);

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
                postMap.put("title", title);
                postMap.put("description", description);
                postMap.put("time", "1");
                postMap.put("assignedEmployeeId", assignedEmployeeId);
                postMap.put("customerId", "1");
                postMap.put("locationId", locationId);
                //..... Add as many key value pairs in the map as necessary for your request
                return postMap;
            }
        };
//make the request to your server as indicated in your request url
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);

    }



// Add the request to the RequestQueue.


}

