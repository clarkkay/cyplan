package com.example.frontend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.frontend.helpers.GlobalVariableHelper;
import com.example.frontend.helpers.SessionManagement;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class FriendSavedPlansActivity extends AppCompatActivity {

    String ip = GlobalVariableHelper.ip;
    /**
     * list of schedules for the user
     */
    List<String> scheduleList = new ArrayList<>();

    /**
     * buttons used on the screen
     */
    Button menu;

    /**
     * container to generate the plans into
     */
    LinearLayout plansContainer;

    /**
     * base url
     */
    String url_base = ip + "/home";

    /**
     * urls to put together
     */
    String url_get;

    /**
     * user_plan that was last selected from the session
     */
    String user_plan;

    /**
     * user_id from the session
     */
    int plan_owner_id; //FIXME set this later

    /**
     * request queue to take api requests
     */
    RequestQueue queue;

    /**
     * session management so that we can retrieve information about the user
     * who is logged in
     */
    SessionManagement sessionManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_saved_plans);

        sessionManagement = new SessionManagement(FriendSavedPlansActivity.this);
        // get user_id and plan from the shared preferences
        //TODO
        plan_owner_id = sessionManagement.getPlanOwnerID();
        user_plan = sessionManagement.getUserPlan();

        // put together urls
        url_get = url_base + "/getSchedules?userID=" + plan_owner_id;

        //Instantiate the request queue
        queue = Volley.newRequestQueue(FriendSavedPlansActivity.this);

        // do a GET request to get the plans that a student has saved
        getSchedules();

        menu = findViewById(R.id.menuButton);
    }

    /**
     * populate the schedule container with a button of each of the schedules
     * @param schedules
     */
    private void populateSchedules(List<String> schedules){
        plansContainer = findViewById(R.id.plansContainer);
        for (String schedule : schedules){
            View view = LayoutInflater.from(this).inflate(R.layout.item_saved_plan, plansContainer, false);
            Button savedPlanButton = view.findViewById(R.id.savedPlanButton);

            // Set the text and click listener for the savedPlanButton
            savedPlanButton.setText(schedule);
            savedPlanButton.setOnClickListener(v -> {
                sessionManagement = new SessionManagement(FriendSavedPlansActivity.this);
                sessionManagement.setUserPlan(schedule);
                // go to Home and make GET request of selected plan
                goToHome(null);
            });

            // Add the entire view (which contains the savedPlanButton) to the plansContainer
            plansContainer.addView(view);
        }
    }

    /**
     * go to the home activity
     * @param view
     */
    public void goToHome(View view){
        Intent intent = new Intent(FriendSavedPlansActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * string builder to create the string of plan names
     * @param selectedSchedules
     */
    private String convertPlanNames(List<String> selectedSchedules) {
        StringBuilder sb = new StringBuilder();
        for (String planName : selectedSchedules) {
            try {
                sb.append(URLEncoder.encode(planName, "UTF-8").replace("+", "%20"));
                sb.append(",");
            } catch (UnsupportedEncodingException e) {
                // Handle the exception
                e.printStackTrace();
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);  // Remove the trailing comma
        }

        return sb.toString();
    }


    /**
     * GET request saved schedules by user_id
     */
    public void getSchedules(){
        // make a GET request
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url_get, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        String[] schedules = new String[response.length()];
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                schedules[i] = response.getString(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        for (String item: schedules) {
                            scheduleList.add(item);
                        }
                        populateSchedules(scheduleList);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.toString());
                    }
                });

        // Add the request to the RequestQueue
        queue.add(jsonArrayRequest);
    }

    /**
     * go to the menu
     * @param view
     */
    public void goToMenu(View view) {
        Intent intent = new Intent(FriendSavedPlansActivity.this, DropDownMenu.class);
        startActivity(intent);
        finish();
    }
}