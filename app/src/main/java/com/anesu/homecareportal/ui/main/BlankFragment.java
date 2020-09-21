package com.anesu.homecareportal.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.anesu.homecareportal.HomeActivity;
import com.anesu.homecareportal.R;
import com.anesu.homecareportal.RoomsActivity;
import com.anesu.homecareportal.TaskActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView mRecyclerView;
    private ItemAdapter mAdapter;
    private Button appartments_rooms;
    private RecyclerView.LayoutManager mLayoutManager;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<taskItem> taskLists;
    private Boolean loaded = false;
    private Boolean loadToDoData = false;
    private View mainView;
    private static final int REQUEST_CODE = 10; // Code to send to child, and expect back.
    // Usually have one for each child.
    private boolean tasksCompleted = false;



    public BlankFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment newInstance(String param1, String param2) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        taskLists = new ArrayList<>();
        if (mParam1.equals("to-do")) {

            loadToDoData = true;

            //taskLists.add(new taskItem(R.drawable.ic_android,"Task","Description"));

            //Toast.makeText(getActivity().getApplicationContext(), "Task to Add", Toast.LENGTH_LONG).show();
            //getUCompletedTasksFromServer();
            //taskLists.add(new taskItem(R.drawable.ic_account_balance,"Line 3","Line 4"));
            //taskLists.add(new taskItem(R.drawable.ic_wb_sunny,"Line 5","Line 6"));


        }




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
                Intent intent = new Intent(getContext(), TaskActivity.class);
                intent.putExtra("taskId",taskLists.get(position).getmId());
                intent.putExtra("title",taskLists.get(position).getmText1());
                intent.putExtra("description",taskLists.get(position).getmText3());
                intent.putExtra("locationId",taskLists.get(position).getLocation());
                if (tasksCompleted)
                    intent.putExtra("taskCompleted","completed");
                //Toast.makeText(getActivity().getApplicationContext(), "Task to Add: "+taskLists.get(position).getmId(), Toast.LENGTH_LONG).show();
                //startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE);
                //startActivity(intent);
            }
        });
        mRecyclerView = mainView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void getUCompletedTasksFromServer(final Boolean newTasks) {
            final RequestQueue queueB = Volley.newRequestQueue(getActivity().getApplicationContext());
            String url ="http://externos.io/home_care_portal/get_new_tasks/";
        //Toast.makeText(getActivity().getApplicationContext(), "Task to Add", Toast.LENGTH_LONG).show();
// Request a string response from the provided URL.
            final StringRequest stringRequestB = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                            try {
                                // create the json array from String rules
                                JSONArray jsonRules = new JSONArray(response);
                                // iterate over the rules
                                String checkCompletedString = "0";
                                if (!newTasks) {
                                    checkCompletedString = "1";
                                }

                                String additionalDescription = "";

                                if (tasksCompleted)
                                    additionalDescription = "<p><b>Task Status:</b> Completed</p>";
                                else
                                    additionalDescription = "<p><b>Task Status:</b> Not Completed</p>";

                                additionalDescription += "<h2>Description</h2>";

                                for (int i=0; i<jsonRules.length();i++){
                                    JSONObject obj = (JSONObject) jsonRules.get(i);
                                    //System.out.println("====obj===="+obj);
                                    String completed = obj.getString("taskCompleted");
                                    //Toast.makeText(getActivity().getApplicationContext(), "Number of lines : "+completed, Toast.LENGTH_LONG).show();
                                    if (obj.getString("taskCompleted").equals(checkCompletedString)) {
                                        String title = obj.getString("title");
                                        String description = additionalDescription;//+obj.getString("description");
                                        String[] lineSplit = obj.getString("description").split("\r\n|\r|\n");
                                        String taskId = obj.getString("id");
                                        String locationId = obj.getString("locationId");
                                        String shortDescription = obj.getString("description");

                                        if (lineSplit.length > 4)
                                            shortDescription = "";

                                            for (int j = 0; j < lineSplit.length; j++) {
                                                if (lineSplit.length > 4) {
                                                    if (j < 5)
                                                        shortDescription += lineSplit[j]+"\n";
                                                }


                                                description += "<p>"+lineSplit[j]+"</p>";
                                            }

                                        if (lineSplit.length > 4)
                                            shortDescription += ".....";




                                        //Toast.makeText(getActivity().getApplicationContext(), "Number of lines : "+description.split("\r\n|\r|\n").length, Toast.LENGTH_LONG).show();

                                        //System.out.println("===id is===: "+id);
                                        taskLists.add(new taskItem(R.drawable.ic_assignment_light_black,title,shortDescription,description,taskId,locationId));
                                        //Toast.makeText(getActivity().getApplicationContext(), "Task to Add ("+i+"): "+taskId, Toast.LENGTH_LONG).show();
                                    }

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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_blank, container, false);



        if (loadToDoData)
            getUCompletedTasksFromServer(true);
        else {
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            getUCompletedTasksFromServer(false); tasksCompleted = true;
                        }
                    },
                    1000);
        }



        appartments_rooms = (Button) mainView.findViewById(R.id.viewApprtmentButton);

        appartments_rooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppartmentsActivity();
            }
        });

        // Inflate the layout for this fragment
        return mainView;
    }

    public void openAppartmentsActivity() {
        Intent intent = new Intent(getContext(), RoomsActivity.class);
        startActivity(intent);
    }
}
