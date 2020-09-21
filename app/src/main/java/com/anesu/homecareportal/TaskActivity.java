package com.anesu.homecareportal;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskActivity extends AppCompatActivity {
    private WifiManager wifiManager;
    private List<ScanResult> results;
    private String wiFiTest;
    private String taskTitlteText;
    private String taskDescriptionText;
    private String taskId;
    private String locationId;
    private CollapsingToolbarLayout taskTitle;
    private TextView taskDescription;
    private FloatingActionButton fab;
    private Boolean canSubmitTask; //Used to check if user is in the location
    private View fullView;
    private static final int REQUEST_CODE = 10; // Code to send to child, and expect back.
    private boolean taskCompleted = false;

    class wifiObject {
        String id;
        double minDtance, maxDistance;
        double second;

        public wifiObject() {
            this.id = "";
            this.minDtance = 0;
            this.maxDistance = 0.0;
        }
    }

    private ArrayList<wifiObject> discoveredWifis = new ArrayList<wifiObject>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ServiceCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        canSubmitTask = false; //By default they can't until we are sure

        fullView = (View) findViewById(android.R.id.content);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Mark Task as completed Not yet implemented.", Snackbar.LENGTH_LONG)
                  //      .setAction("Action", null).show();
                if (canSubmitTask) {
                    //Intent intent = new Intent(view.getContext(), SubmitCompletedTaskActivity.class);
                    //startActivity(intent);

                    //taskLists.get(position)
                    Intent intent = new Intent(view.getContext(), SubmitCompletedTaskActivity.class);
                    intent.putExtra("taskId",taskId);
                    intent.putExtra("title",taskTitlteText);
                    //startActivity(intent);
                    startActivityForResult(intent, REQUEST_CODE);

                } else {
                    Snackbar.make(view, "You are currently not in the location for this task.", Snackbar.LENGTH_LONG)
                          .setAction("Action", null).show();
                    if (discoveredWifis.size() > 0)
                        scanWifi(); //Check User location
                    else
                        Snackbar.make(fullView, "Error getting location information for this task", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                }

            }
        });

        taskDescription = (TextView) findViewById(R.id.taskDescription);//taskDescription
        taskTitle = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        Intent intent = getIntent();
        if (intent.hasExtra("title"))
            taskTitlteText = intent.getStringExtra("title");
        else
            taskTitlteText = "Error obtaining Task data";

        if (intent.hasExtra("taskId"))
            taskId = intent.getStringExtra("taskId");
        else
            taskId = "-1";

        if (intent.hasExtra("description"))
            taskDescriptionText = intent.getStringExtra("description");
        else
            taskDescriptionText = "Error obtaining Task data";

        if (intent.hasExtra("taskCompleted")) {
            Log.e("has completed:","true");
            taskCompleted = true;
        }

        taskTitle.setTitle(taskTitlteText);

        if (intent.hasExtra("locationId"))
            locationId = intent.getStringExtra("locationId");
        else
            locationId = "Error obtaining Task data";



            taskDescription.setText(Html.fromHtml(taskDescriptionText));

            if (!taskCompleted) {
                wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                assert wifiManager != null;
                if (!wifiManager.isWifiEnabled()) {
                    Toast.makeText(this,"WiFi is disabled. Please turn on your WiFi to continue",Toast.LENGTH_LONG).show();
                    wifiManager.setWifiEnabled(true);
                } else {
                    //scanWifi();
                }

                getTaskLocation();
            } else {
                fab.setVisibility(View.GONE);
            }



    }

    private void getTaskLocation() {

        String requestUrl = "https://externos.io/home_care_portal/get_specific_location/";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Volley Resultx", ""+response); //the response contains the result from the server, a json string or any other object returned by your server
                //taskDescription.setText(response);
                try {
                    if (response.equals("0 results")) {
                        Snackbar.make(fullView, "Error getting location information for this task", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        // create the json array from String rules
                        JSONArray jsonRules = new JSONArray(response);

                        //Log.e("res found: ", String.valueOf(jsonRules.length()));
                        // iterate over the rules
                        for (int i=0; i<jsonRules.length();i++){
                            JSONObject obj = (JSONObject) jsonRules.get(i);


                            wifiObject wifiToAdd = new wifiObject();
                            wifiToAdd.id = obj.getString("name");
                            wifiToAdd.minDtance = Double.parseDouble(obj.getString("minDtance"));
                            wifiToAdd.maxDistance = Double.parseDouble(obj.getString("maxDistance"));;
                            discoveredWifis.add(wifiToAdd);
                            //Toast.makeText(getActivity().getApplicationContext(), "Number of lines : "+description.split("\r\n|\r|\n").length, Toast.LENGTH_LONG).show();

                            //System.out.println("===id is===: "+id);
                            //taskLists.add(new taskItem(R.drawable.ic_assignment_light_black,title,shortDescription,description));
                            //Toast.makeText(getActivity().getApplicationContext(), "Task to Add ("+i+"): "+title, Toast.LENGTH_LONG).show();
                        }
                        if (discoveredWifis.size() > 0)
                            scanWifi(); //Check User location
                        else
                            Snackbar.make(fullView, "Error getting location information for this task", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                    }

            } catch (
            JSONException e){
                e.printStackTrace();
            }

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
                //Log.e("locationIdx: ",locationId);
                postMap.put("locationId", locationId);
                //..... Add as many key value pairs in the map as necessary for your request
                return postMap;
            }
        };
//make the request to your server as indicated in your request url
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);

    }

    private void scanWifi() {
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this,"Checking your location using WiFi signals....",Toast.LENGTH_SHORT).show();
    }

    public double calculateDistance(double signalLevelInDb, double freqInMHz) {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);
            wiFiTest = "";
            //Checking if you are still in the area
            double distance;
            Double totalSimilar = 0.0;
            Double failedCheck = 0.0;
            boolean wifiAlreadExists = false;
            for (ScanResult scanResult : results) {
                distance = calculateDistance((double) scanResult.level, (double) scanResult.frequency);
                wifiAlreadExists = false;
                for (wifiObject recordedWifi : discoveredWifis) {
                    if (recordedWifi.id.equals(scanResult.BSSID)) {
                        wifiAlreadExists = true;
                        totalSimilar++;
                        if (distance > recordedWifi.maxDistance || distance < recordedWifi.minDtance) {
                            failedCheck++;
                        }
                    }
                }
                double failed_percent = failedCheck / totalSimilar;
                if (failed_percent >= 0.4) {

                    //taskDescription.setText("You are curruntly NOT in the tagged apartment/room.");
                    canSubmitTask = false;
                    Snackbar.make(fullView, "You cannot submit this task because are currently not in the specified location for this task.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fab.setBackgroundTintList(ColorStateList.valueOf(Color
                            .parseColor("#FFCC00")));
                } else {
                    //From: https://stackoverflow.com/questions/30966222/change-color-of-floating-action-button-from-appcompat-22-2-0-programmatically
                    canSubmitTask = true;
                    fab.setBackgroundTintList(ColorStateList.valueOf(Color
                            .parseColor("#32cd32")));
                    //taskDescription.setText("You are curruntly in this apartment/room.");
                }

            }
        }
    };
}
