package com.example.frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import androidx.core.content.ContextCompat;

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


public class SavedPlansActivity extends AppCompatActivity {

    String ip = GlobalVariableHelper.ip;
    /**
     * list of schedules for the user
     */
    List<String> scheduleList = new ArrayList<>();

    /**
     * mode setter for delete mode
     */
    Boolean mode = true;

    /**
     * list of schedules selected to delete
     */
    List<String> selectedSchedules = new ArrayList<>();

    /**
     * buttons used on the screen
     */
    Button deletePlansButton, menu;

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
    String url_get, url_delete;

    /**
     * user_plan that was last selected from the session
     */
    String user_plan, plan_names;

    /**
     * user_id from the session
     */
    int plan_owner_id, user_id;


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
        setContentView(R.layout.activity_saved_plans);

        sessionManagement = new SessionManagement(SavedPlansActivity.this);
        // get user_id and plan from the shared preferences
        plan_owner_id = sessionManagement.getPlanOwnerID();
        user_id = sessionManagement.getUserId();
        user_plan = sessionManagement.getUserPlan();

        deletePlansButton = findViewById(R.id.deletePlansButton);

        if (plan_owner_id != user_id){
            // it is not editable
            deletePlansButton.setVisibility(View.GONE);
        }

        // put together urls
        url_get = url_base + "/getSchedules?userID=" + plan_owner_id;

        //Instantiate the request queue
        queue = Volley.newRequestQueue(SavedPlansActivity.this);

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
                sessionManagement = new SessionManagement(SavedPlansActivity.this);
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
        Intent intent = new Intent(SavedPlansActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * toggle delete mode
     * @param view
     */
    public void deleteMode(View view){
        mode = !mode; // Toggle mode
        delete(mode);
    }

    /**
     * allow the user to select and delete plans based on the delete mode toggle state
     * @param mode
     */
    private void delete(boolean mode){
        if (!mode) {
            // If delete mode is false
            for (int i = 0; i < plansContainer.getChildCount(); i++) {
                View view = plansContainer.getChildAt(i);
                Button savedPlanButton = view.findViewById(R.id.savedPlanButton);
                // set the color of each button to gray
                savedPlanButton.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));
                // set a click listener to turn the color to red and remove it from the list
                savedPlanButton.setOnClickListener(v -> {
                    // if it's already selected turn it back to gray and take it off list
                    if (selectedSchedules.contains(savedPlanButton.getText().toString())) {
                        selectedSchedules.remove(savedPlanButton.getText().toString());
                        savedPlanButton.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));
                        // otherwise put it in list and turn it to red
                    } else {
                        selectedSchedules.add(savedPlanButton.getText().toString());
                        savedPlanButton.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                    }
                });
            }
            deletePlansButton.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
        } else {
            // If delete mode is true
            // Make a DELETE request to delete the schedules in the list
            plan_names = convertPlanNames(selectedSchedules);
            url_delete = url_base + "/deletePlan?userID=" + plan_owner_id + "&planNames=" + plan_names;
            deleteSchedule();
            // remove them from the testList also for speed
            scheduleList.removeAll(selectedSchedules);
            selectedSchedules.clear();

            deletePlansButton.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));

            // Refresh the view with the updated list
            plansContainer.removeAllViews();
            populateSchedules(scheduleList);
        }
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

    // DELETE request to delete a schedule
    public void deleteSchedule(){
        // make a DELETE request
        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, url_delete,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response here
                        Log.d("DELETE Response", response);
                        Toast.makeText(SavedPlansActivity.this, "Plans Deleted", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error here
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        queue.add(deleteRequest);
    }

    /**
     * go to the menu
     * @param view
     */
    public void goToMenu(View view) {
        int user_id = sessionManagement.getUserId();
        sessionManagement.setPlanOwnerID(user_id);
        Intent intent = new Intent(SavedPlansActivity.this, DropDownMenu.class);
        startActivity(intent);
        finish();
    }
}