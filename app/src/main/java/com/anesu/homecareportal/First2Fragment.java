package com.anesu.homecareportal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.anesu.homecareportal.ui.main.ItemAdapter;
import com.anesu.homecareportal.ui.main.taskItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class First2Fragment extends Fragment {
    private RecyclerView mRecyclerView;
    private ItemAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<taskItem> taskLists;
    private  View mainView;
    private TextView textview_first;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        taskLists = new ArrayList<>();
        /*taskLists.add(new taskItem(R.drawable.ic_android,"Task","Description","lol"));
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new ItemAdapter(taskLists);*/



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first2, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainView = view;
        /*mRecyclerView = view.findViewById(R.id.recyclerViewLocations);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);*/

        textview_first = (TextView) view.findViewById(R.id.textview_first);
        getLocationsFromServer();

        view.findViewById(R.id.addApartmentFab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(First2Fragment.this)
                        .navigate(R.id.action_First2Fragment_to_Second2Fragment);
            }
        });
    }

    private void loadList() {
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new ItemAdapter(taskLists);
        //taskLists.add(new taskItem(R.drawable.ic_android,"Task","Description"));
        //getUCompletedTasksFromServer();
        //Toast.makeText(getActivity().getApplicationContext(), "test"+taskLists.size(), Toast.LENGTH_LONG).show();
        //taskLists.add(new taskItem(R.drawable.ic_android,"Task","Description"));

        mAdapter.setOnItemClickListener(new ItemAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //taskLists.get(position)
                /*Intent intent = new Intent(getContext(), TaskActivity.class);
                intent.putExtra("title",taskLists.get(position).getmText1());
                intent.putExtra("description",taskLists.get(position).getmText3());
                //startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE);*/
                //startActivity(intent);
            }
        });
        mRecyclerView = mainView.findViewById(R.id.recyclerViewLocations);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void getLocationsFromServer() {
        RequestQueue queueB = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url ="http://externos.io/home_care_portal/get_locations/";
        //Toast.makeText(getActivity().getApplicationContext(), "Task to Add", Toast.LENGTH_LONG).show();
// Request a string response from the provided URL.
        StringRequest stringRequestB = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            // create the json array from String rules
                            JSONArray jsonRules = new JSONArray(response);
                            // iterate over the rules

                            if (jsonRules.length() > 0) {
                                textview_first.setText(""); //Remove the empty list text
                            } else {
                                textview_first.setText("You haven't added any Apartments");
                            }
                            for (int i=0; i<jsonRules.length();i++){
                                JSONObject obj = (JSONObject) jsonRules.get(i);
                                //System.out.println("====obj===="+obj);

                                String id = obj.getString("id");
                                String name = obj.getString("name");
                                //Toast.makeText(getContext(), "Name : "+name, Toast.LENGTH_LONG).show();

                                //System.out.println("===id is===: "+id);
                                taskLists.add(new taskItem(R.drawable.ic_location_on_black,name,"Location","Location","0",""));
                                //Toast.makeText(getActivity().getApplicationContext(), "Task to Add ("+i+"): "+title, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                        loadList();
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
}
