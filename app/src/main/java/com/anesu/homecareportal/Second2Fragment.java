package com.anesu.homecareportal;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Second2Fragment extends Fragment {
    private View fragView;
    private ImageView leftTopCornerIndicator;
    private ImageView rightTopCornerIndicator;
    private ImageView leftBottomCornerIndicator;
    private ImageView rightBottomCornerIndicator;
    private View topRoomDivider;
    private View bottomRoomDivider;
    private ImageView leftTopCorner;
    private ImageView leftBottomCorner;
    private ImageView rightTopCorner;
    private ImageView rightBottomCorner;
    private Button tagCornerButton;
    private TextView tagInstextView;
    private TextView enterNametextView;
    private FloatingActionButton doneApartmentConfigfab;
    private EditText ApartmentNameeditText;
    private Boolean doneSetUp;
    private WifiManager wifiManager;
    private List<ScanResult> results;
    private List<String> BSSIDs;
    private String placeName;
    private String wiFiTest;
    private TextView taggingProgressTextView;
    private int currentCorner;
    private Button button_second;
    private Boolean detectingWifiMode;
    private Button checkIfStillInAreaButton;
    private Button goBackButton;

    private boolean scanInit;

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

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second2, container, false);
    }


    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        leftTopCornerIndicator = (ImageView) view.findViewById(R.id.leftTopCornerIndicator);
        leftBottomCornerIndicator = (ImageView) view.findViewById(R.id.leftBottomCornerIndicator);
        rightTopCornerIndicator = (ImageView) view.findViewById(R.id.rightTopCornerIndicator);
        rightBottomCornerIndicator = (ImageView) view.findViewById(R.id.rightBottomCornerIndicator);
        leftTopCorner = (ImageView) view.findViewById(R.id.leftTopCorner);
        leftBottomCorner = (ImageView) view.findViewById(R.id.leftBottomCorner);
        rightTopCorner = (ImageView) view.findViewById(R.id.rightTopCorner);
        rightBottomCorner = (ImageView) view.findViewById(R.id.rightBottomCorner);
        topRoomDivider = (View)  view.findViewById(R.id.topRoomDivider);
        bottomRoomDivider = (View)  view.findViewById(R.id.bottomRoomDivider);
        tagCornerButton = (Button)  view.findViewById(R.id.tagCornerButton);
        tagInstextView = (TextView)  view.findViewById(R.id.tagInstextView);
        enterNametextView = (TextView)  view.findViewById(R.id.enterNametextView);
        ApartmentNameeditText = (EditText)  view.findViewById(R.id.ApartmentNameeditText);
        doneApartmentConfigfab = (FloatingActionButton)  view.findViewById(R.id.doneApartmentConfigfab);
        taggingProgressTextView = (TextView)  view.findViewById(R.id.taggingProgressTextView);
        button_second = (Button) view.findViewById(R.id.goBackButton);
        checkIfStillInAreaButton = (Button)  view.findViewById(R.id.checkIfStillInAreaButton);
        scanInit = false;
        currentCorner = 0;
        detectingWifiMode = true;


        taggingProgressTextView.setVisibility(View.GONE);
        checkIfStillInAreaButton.setVisibility(View.GONE);

        tagInstextView.setText("Go to the corner pointed in the diagram and then press \"TAG\"");

        doneSetUp = false;

        /*ApartmentNameeditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //if (!hasFocus) {
                    // code to execute when EditText loses focus
                    if (ApartmentNameeditText.getText().toString().equals("")) {
                        hideRoomSelector();
                        enterNametextView.setVisibility(View.VISIBLE);
                    } else {
                        showRoomSelector();
                        enterNametextView.setVisibility(View.GONE);
                    }
                //}
            }
        })*/;

        ApartmentNameeditText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (ApartmentNameeditText.getText().toString().equals("")) {
                    hideRoomSelector();
                    restoreSetupStage();
                    enterNametextView.setVisibility(View.VISIBLE);
                } else {
                    showRoomSelector();
                    restoreSetupStage();
                    enterNametextView.setVisibility(View.GONE);
                    placeName = ApartmentNameeditText.getText().toString();
                }

                // you can call or do what you want with your EditText here

                // yourEditText...
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        doneApartmentConfigfab.hide();

        hideRoomSelector();

        tagCornerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert wifiManager != null;
                if (!wifiManager.isWifiEnabled()) {
                    Toast.makeText(getContext(),"WiFi is disabled. Please turn on your WiFi to continue",Toast.LENGTH_LONG).show();
                    wifiManager.setWifiEnabled(true);
                } else {
                    tagCornerButton.setVisibility(View.GONE);
                    tagInstextView.setVisibility(View.GONE);
                    taggingProgressTextView.setText("Tagging Corner: 50%");
                    taggingProgressTextView.setVisibility(View.VISIBLE);
                    scanInit = true;
                    scanWifi();
                }
            }
        });

        checkIfStillInAreaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUserInsideArea();
            }
        });

        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        assert wifiManager != null;
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getContext(),"WiFi is disabled. Please turn on your WiFi to continue",Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        view.findViewById(R.id.goBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(Second2Fragment.this)
                        .navigate(R.id.action_Second2Fragment_to_First2Fragment);
            }
        });




        //fragView = view;
        doneApartmentConfigfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTaskToRemoteDB();
                //NavHostFragment.findNavController(Second2Fragment.this)
                 //       .navigate(R.id.action_Second2Fragment_to_First2Fragment);
            }
        });
    }

    private void addTaskToRemoteDB() {

        String requestUrl = "https://externos.io/home_care_portal/add_new_location/";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Volley Resultx", ""+response); //the response contains the result from the server, a json string or any other object returned by your server
                //Fixme: Error checking
                NavHostFragment.findNavController(Second2Fragment.this)
                       .navigate(R.id.action_Second2Fragment_to_First2Fragment);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                JSONArray jsonArray = new JSONArray();
                try {
                for (wifiObject recordedWifi : discoveredWifis) {
                    JSONObject wifiObject = new JSONObject();
                    wifiObject.put("name",recordedWifi.id);
                    wifiObject.put("maxDistance",recordedWifi.maxDistance);
                    wifiObject.put("minDtance",recordedWifi.minDtance);
                    jsonArray.put(wifiObject);
                }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Map<String, String> postMap = new HashMap<>();
                postMap.put("name", ApartmentNameeditText.getText().toString());
                postMap.put("wifiData", jsonArray.toString());
                //..... Add as many key value pairs in the map as necessary for your request
                return postMap;
            }
        };
//make the request to your server as indicated in your request url
        Volley.newRequestQueue(getContext()).add(stringRequest);

    }

    private void moveToNextCorner() {
        if (currentCorner == 0) {
            leftTopCorner.setImageResource(R.drawable.ic_check_circle_green);
            leftTopCornerIndicator.setVisibility(View.GONE);
            leftBottomCornerIndicator.setVisibility(View.VISIBLE);
            currentCorner++;
        } else if (currentCorner == 1) {
            leftTopCorner.setImageResource(R.drawable.ic_check_circle_green);
            leftBottomCorner.setImageResource(R.drawable.ic_check_circle_green);
            leftTopCornerIndicator.setVisibility(View.GONE);
            leftBottomCornerIndicator.setVisibility(View.GONE);
            rightBottomCornerIndicator.setVisibility(View.VISIBLE);
            currentCorner++;
        } else if (currentCorner == 2) {
            leftTopCorner.setImageResource(R.drawable.ic_check_circle_green);
            leftBottomCorner.setImageResource(R.drawable.ic_check_circle_green);
            rightBottomCorner.setImageResource(R.drawable.ic_check_circle_green);
            leftTopCornerIndicator.setVisibility(View.GONE);
            leftBottomCornerIndicator.setVisibility(View.GONE);
            rightBottomCornerIndicator.setVisibility(View.GONE);
            rightTopCornerIndicator.setVisibility(View.VISIBLE);
            currentCorner++;
        } else if (currentCorner == 3) {
            leftTopCorner.setImageResource(R.drawable.ic_check_circle_green);
            leftBottomCorner.setImageResource(R.drawable.ic_check_circle_green);
            rightBottomCorner.setImageResource(R.drawable.ic_check_circle_green);
            rightTopCorner.setImageResource(R.drawable.ic_check_circle_green);
            leftTopCornerIndicator.setVisibility(View.GONE);
            leftBottomCornerIndicator.setVisibility(View.GONE);
            rightBottomCornerIndicator.setVisibility(View.GONE);
            rightTopCornerIndicator.setVisibility(View.GONE);
            tagCornerButton.setVisibility(View.GONE);
            taggingProgressTextView.setVisibility(View.GONE);
            doneApartmentConfigfab.show();
            tagInstextView.setText("You are curruntly in this apartment/room.");
            tagInstextView.setVisibility(View.VISIBLE);
            checkIfStillInAreaButton.setVisibility(View.VISIBLE);
            button_second.setVisibility(View.GONE);
            currentCorner = 0;
            detectingWifiMode = false;

        }
    }

    private void isUserInsideArea() {
        getActivity().registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(getActivity(),"Checking...",Toast.LENGTH_SHORT).show();
    }

    private void scanWifi() {
        getActivity().registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(getActivity(),"Scanning...",Toast.LENGTH_SHORT).show();
    }

    public double calculateDistance(double signalLevelInDb, double freqInMHz) {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            getActivity().unregisterReceiver(this);
            if (detectingWifiMode) {
                wiFiTest = "";
                Double distance;
                Boolean wifiAlreadExists = false;
                for (ScanResult scanResult : results) {
                    distance = calculateDistance((double) scanResult.level,(double) scanResult.frequency);
                    wifiAlreadExists = false;
                    for (wifiObject recordedWifi : discoveredWifis) {
                        if (recordedWifi.id.equals(scanResult.BSSID)) {
                            wifiAlreadExists = true;
                            if (distance > recordedWifi.maxDistance) {
                                recordedWifi.maxDistance = distance;
                            } else if (distance < recordedWifi.minDtance) {
                                recordedWifi.minDtance = distance;
                            }
                        }
                    }
                    if (!wifiAlreadExists) {
                        wifiObject wifiToAdd = new wifiObject();
                        wifiToAdd.id = scanResult.BSSID;
                        wifiToAdd.minDtance = distance;
                        wifiToAdd.maxDistance = distance;
                        discoveredWifis.add(wifiToAdd);
                    }
                    //wiFiTest += "name: "+scanResult.SSID+" level: "+scanResult.level+" frequency: "+scanResult.frequency+" Distance: "+calculateDistance((double) scanResult.level,(double) scanResult.frequency)+"\n";
                }
                //tagInstextView.setText(wiFiTest);

                if (scanInit) {
                    taggingProgressTextView.setText("Tagging Corner: 90%");
                    scanInit = false;
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    scanWifi();
                                }
                            },
                            500);
                } else {
                    taggingProgressTextView.setText("Tagging Corner: 100%");
                    tagCornerButton.setVisibility(View.VISIBLE);
                    tagInstextView.setVisibility(View.VISIBLE);
                    taggingProgressTextView.setVisibility(View.GONE);
                    moveToNextCorner();
                    if (currentCorner != 0)
                        Toast.makeText(getContext(),"Done. Now move to the highlighted corner.",Toast.LENGTH_LONG).show();
                    else
                        isUserInsideArea();
                }
            } else {
                //Checking if you are still in the area
                Double distance;
                Double totalSimilar = 0.0;
                Double failedCheck = 0.0;
                Boolean wifiAlreadExists = false;
                for (ScanResult scanResult : results) {
                    distance = calculateDistance((double) scanResult.level,(double) scanResult.frequency);
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
                    Double failed_percent = failedCheck/totalSimilar;
                    if (failed_percent >= 0.4)
                        tagInstextView.setText("You are curruntly NOT in the tagged apartment/room.");
                    else
                        tagInstextView.setText("You are curruntly in this apartment/room.");


                    /*handler.postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    isUserInsideArea();
                                }
                            },
                            5000);*/
                    //wiFiTest += "name: "+scanResult.SSID+" level: "+scanResult.level+" frequency: "+scanResult.frequency+" Distance: "+calculateDistance((double) scanResult.level,(double) scanResult.frequency)+"\n";
                }
            }


        }
    };


            public void hideRoomSelector() {
        leftTopCornerIndicator.setVisibility(View.GONE);
        leftBottomCornerIndicator.setVisibility(View.GONE);
        rightTopCornerIndicator.setVisibility(View.GONE);
        rightBottomCornerIndicator.setVisibility(View.GONE);
        topRoomDivider.setVisibility(View.GONE);
        bottomRoomDivider.setVisibility(View.GONE);
        leftTopCorner.setVisibility(View.GONE);
        leftBottomCorner.setVisibility(View.GONE);
        rightTopCorner.setVisibility(View.GONE);
        rightBottomCorner.setVisibility(View.GONE);
        tagCornerButton.setVisibility(View.GONE);
        tagInstextView.setVisibility(View.GONE);
    }

    public void showRoomSelector() {
        /*leftTopCornerIndicator.setVisibility(View.VISIBLE);
        leftBottomCornerIndicator.setVisibility(View.VISIBLE);
        rightTopCornerIndicator.setVisibility(View.VISIBLE);
        rightBottomCornerIndicator.setVisibility(View.VISIBLE);*/
        topRoomDivider.setVisibility(View.VISIBLE);
        bottomRoomDivider.setVisibility(View.VISIBLE);
        leftTopCorner.setVisibility(View.VISIBLE);
        leftBottomCorner.setVisibility(View.VISIBLE);
        rightTopCorner.setVisibility(View.VISIBLE);
        rightBottomCorner.setVisibility(View.VISIBLE);
        tagCornerButton.setVisibility(View.VISIBLE);
        tagInstextView.setVisibility(View.VISIBLE);
    }

    public void restoreSetupStage() {
        if (!doneSetUp) {
            leftTopCornerIndicator.setVisibility(View.VISIBLE);
        }
    }



    public void openSecondFragment() {
        NavHostFragment.findNavController(Second2Fragment.this)
                .navigate(R.id.action_Second2Fragment_to_First2Fragment);
    }
}
