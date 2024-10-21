package com.example.frontend;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.example.frontend.helpers.GlobalVariableHelper;
import com.example.frontend.helpers.SessionManagement;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.example.frontend.helpers.websockets.WebSocketListener;
import com.example.frontend.helpers.websockets.WebSocketManager;

import org.java_websocket.handshake.ServerHandshake;

/**
 * home activity is the schedule builder in which a student can create a schedule,
 * post a schedule, update a schedule, and use a websocket to post and view comments
 * about specific courses
 */
public class HomeActivity extends AppCompatActivity implements WebSocketListener {

    // urls
    String ip = GlobalVariableHelper.ip;
    String websocket_ip = GlobalVariableHelper.websocket_ip;
    private String websocket_url_base = websocket_ip + "/courseComments/";
    private String websocket_url_rating_base = websocket_ip + "/courseRating/";
    private String url_base = ip + "/home";
    private String url_get_plan_default, url_get_plan, url_post, url_get_description, url_get_comments, url_post_comment;

    // Lists to hold courses to be generated and comments to be generated
    private List<String> courseComments = new ArrayList<>();

    // public for test case purposes
    public List<List<String>> semesterList = new ArrayList<>();

    // string that are used throughout
    private String course_details, plan_name, user_plan, user_type, courseCode;

    // layout to fill
    private GridLayout semesterContainer;

    // ArrayAdapter to show the course comments and the current one
    private ArrayAdapter<String> commentsAdapter;


    // name of the schedule that user fills in
    private EditText schedule_name;

    // user_id gotten by the session
    private int user_id;

    private RequestQueue queue;
    private SessionManagement sessionManagement;

    private AlertDialog courseDialog;
    String url_chats = ip;

    private ListView commentsList;
    private TextView courseDetailsText;
    private EditText commentInput, advisorCommentSection;
    private RatingBar ratingBar;
    private Float courseRating;
    private SearchView search;
    private boolean isEditable = true;
    private int plan_owner_id;
    private Button startOverButton, addSemesterButton, savePlanButton, goToSavedPlansButton;

    private Map<String, List<String>> courseClusters = new HashMap<>();
    private Map<String, String> courseClickTesting = new HashMap<>();

    // public for testing reasons
    public String alertMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // get user_id and plan from the shared preferences
        sessionManagement = new SessionManagement(HomeActivity.this);
        user_id = sessionManagement.getUserId();
        user_plan = sessionManagement.getUserPlan();
        user_type = sessionManagement.getUserType();
        try {
            plan_owner_id = sessionManagement.getPlanOwnerID();
        } catch (Exception e){
            plan_owner_id = user_id;
        }
        if (plan_owner_id != user_id){
            isEditable = false;
        }

        // put together the urls
        url_get_plan_default = url_base + "/getBasePlan?userID=" + user_id;
        if (!isEditable){
            url_get_plan = url_base + "?userID=" + plan_owner_id + "&planName=" + plan_name;
        } else {
            url_get_plan = url_base + "?userID=" + user_id + "&planName=" + plan_name;
        }
        url_post = url_base + "/addPlan";

        // set everything that is needed from the xml
        TextView scheduleInfoTextView = findViewById(R.id.scheduleInfoTextView);
        scheduleInfoTextView.setText(user_plan);
        schedule_name = findViewById(R.id.scheduleNameEditText);
        semesterContainer = findViewById(R.id.semesterContainer);
        search = findViewById(R.id.searchCourse);
        advisorCommentSection = findViewById(R.id.advisorCommentsEditText);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // called when the user submits the search by pressing the search icon or enter
                String course = search.getQuery().toString();
                searchForCourse(course);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // at this time we don't want to query when the text changes, only on submit
                return false;
            }
        });
        startOverButton = findViewById(R.id.startOverButton);
        addSemesterButton = findViewById(R.id.addSemesterButton);
        savePlanButton = findViewById(R.id.savePlanButton);
        goToSavedPlansButton = findViewById(R.id.goToSavedPlansButton);

        // if isEditable is false, make all of the buttons disappear
        if (!isEditable) {
            schedule_name.setVisibility(View.GONE);
            startOverButton.setVisibility(View.GONE);
            addSemesterButton.setVisibility(View.GONE);
            savePlanButton.setVisibility(View.GONE);
            goToSavedPlansButton.setVisibility(View.GONE);
        }

        //Instantiate the request queue
        queue = Volley.newRequestQueue(HomeActivity.this);

        // get the default schedule for the user from their major or last selected plan
        if (user_plan == null){
            getDefaultSchedule();
        } else {
            plan_name = sessionManagement.getUserPlan();
            schedule_name.setText(plan_name);
            getPlanComments();
        }
    }

    /**
     * method overloading to handle optional param in java
     * typically in python would just say optional on the param
     * but we can't do that in java :(
     * @param semesters
     */
    private void populateSchedule(List<List<String>> semesters) {
        String[] alertCourses = {};
        populateSchedule(semesters, alertCourses);
    }


    /**
     * for each semester, add the semester to the layout file
     *
     * @param semesters
     */
    private void populateSchedule(List<List<String>> semesters, String[] alertCourses) {
        for (int i = 0; i < semesters.size(); i++) {
            LinearLayout semesterPairContainer = new LinearLayout(this);
            semesterPairContainer.setOrientation(LinearLayout.HORIZONTAL);
            semesterPairContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            // Add current semester
            View semesterView1 = createSemesterView(semesters.get(i), i, alertCourses);
            semesterPairContainer.addView(semesterView1);

            // If there's a next semester, add it
            if (i + 1 < semesters.size()) {
                View semesterView2 = createSemesterView(semesters.get(i + 1), i + 1, alertCourses);
                semesterPairContainer.addView(semesterView2);
            }

            semesterContainer.addView(semesterPairContainer);
        }
    }


    /**
     * for each class in the specified semester, add the class to the layout file
     *
     * @param courses
     * @param semesterNumber
     * @return
     */
    private View createSemesterView(List<String> courses, int semesterNumber, String[] alertCourses) {
        View semesterView = LayoutInflater.from(this).inflate(R.layout.item_semester, null, false);
        setupDrop(semesterView, semesterNumber);
        TextView semesterTitle = semesterView.findViewById(R.id.semesterTitle);
        if (semesterNumber == 0){
            semesterTitle.setText("Completed");
        } else {
            semesterTitle.setText("Semester " + semesterNumber);
        }

        LinearLayout coursesContainer = semesterView.findViewById(R.id.coursesContainer);
        for (String course : courses) {
            TextView courseView = (TextView) LayoutInflater.from(this).inflate(R.layout.item_course, coursesContainer, false);
            courseView.setText(course);

            // Check if the course is in the alertCourses array
            if (Arrays.asList(alertCourses).contains(course)) {
                courseView.setBackgroundColor(getResources().getColor(R.color.red));
            }
            // Check if the course if a cluster
            if (courseClusters.containsKey(course)){
                courseView.setBackgroundResource(R.drawable.background_with_outline);
            }

            coursesContainer.addView(courseView);
            // only needed for testing purposes
            if (!courseClickTesting.containsKey(course)){
                courseView.setContentDescription(course + " Semester " + semesterNumber);
                courseClickTesting.put(course, course + " Semester " + semesterNumber);
            }
            // Set up drag for the course view
            setupDrag(courseView, course);
            if (courseClusters.containsKey(course)){
                courseView.setOnClickListener(v -> {
                    try {
                        showCoursePopup(course);
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                courseView.setOnClickListener(v -> getCourseDescription(course));
            }
        }

        return semesterView;
    }

    /**
     * helper to set the details and the pop up for a course and allow for users to send and receive
     * comments via a websocket
     * @param course
     */
    private void showCoursePopup(String course) throws UnsupportedEncodingException {

        // set up the view
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_comments, null);

        // get the info from the view
        courseDetailsText = view.findViewById(R.id.course_details_text);
        commentsList = view.findViewById(R.id.comments_list);
        commentInput = view.findViewById(R.id.comment_input);
        ratingBar = view.findViewById(R.id.ratingBar);
        LinearLayout courseOptionsContainer = view.findViewById(R.id.courseOptionsContainer);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle(course);

        // on click of "Exit", dismiss and disconnect
        builder.setNegativeButton("Exit", (dialog, which) -> {
            courseDialog.dismiss();
            WebSocketManager.getInstance().disconnectAllWebSockets();
            courseComments.clear();
            commentsList.setAdapter(null);
            commentsAdapter = null;
        });

        // if it is a course cluster
        if (courseClusters.containsKey(course)){

            // don't show the course ratings
            courseDetailsText.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
            commentsList.setVisibility(View.GONE);
            commentInput.setVisibility(View.GONE);

            // populate the course pop up with the popups from that cluster
            List<String> options = courseClusters.get(course);
            for (String option : options){
                View buttonView = LayoutInflater.from(this).inflate(R.layout.item_saved_plan, courseOptionsContainer, false);
                Button courseButton = buttonView.findViewById(R.id.savedPlanButton);

                // Set the text and click listener for the savedPlanButton
                courseButton.setText(option);
                // allow user to choose course from cluster
                courseButton.setOnClickListener(v -> {

                    // Replace the specific course
                    boolean replaced = false;
                    for (List<String> sublist : semesterList) {
                        for (int i = 0; i < sublist.size(); i++) {
                            if (sublist.get(i).equals(course)) {
                                sublist.set(i, option);
                                replaced = true;
                                break;
                            }
                        }
                        if (replaced) {
                            break;
                        }
                    }
                    // remove course cluster key and value pair that was chosen
                    courseClusters.remove(course);
                    // close the popup
                    courseDialog.dismiss();
                    // refresh the view
                    semesterContainer.removeAllViews();
                    populateSchedule(semesterList);

                });

                // Add the button
                courseOptionsContainer.addView(buttonView);
            }

        } else {
            courseOptionsContainer.setVisibility(View.GONE);

            // set up websocket url
            String encodedCourse = URLEncoder.encode(course, "UTF-8");
            String websocket_url = websocket_url_base + encodedCourse;
            String websocket_rating_url = websocket_url_rating_base + encodedCourse;

            // Establish WebSocket connection and set listener
            if (WebSocketManager.getInstance().isConnected()) {
                WebSocketManager.getInstance().disconnectAllWebSockets();
            }
            WebSocketManager.getInstance().connectWebSocket("comment", websocket_url);
            WebSocketManager.getInstance().connectWebSocket("rating", websocket_rating_url);
            WebSocketManager.getInstance().setWebSocketListener(HomeActivity.this);

            courseDetailsText.setText(course_details);
            ratingBar.setRating(courseRating);

            // Create and set the adapter
            commentsAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    courseComments
            );
            commentsList.setAdapter(commentsAdapter);

            // on click of "OK", send the comment
            builder.setPositiveButton("Comment", (dialog, which) -> {
                // Capture the comment from the input
                String comment = commentInput.getText().toString().trim();
                try {
                    // send message
                    if (!comment.isEmpty()){
                        WebSocketManager.getInstance().sendMessage("comment", comment);
                    }
                } catch (Exception e) {
                    Log.d("ExceptionSendMessage:", e.getMessage().toString());
                }
                // also post the comment to the database
                postCourseComment(course, comment);
                // Clear the input
                commentInput.setText("");
            });

            if (user_type == "A"){
                // allow the advisor to revert the ratings
                builder.setNeutralButton("Reset Rating", (dialog, which) -> {
                    try {
                        // send rating
                        resetCourseRating(course);
                    } catch (Exception e) {
                        Log.d("ExceptionSendMessage:", e.getMessage().toString());
                    }
                    // update the rating
                });
            } else {
                builder.setNeutralButton("Rate", (dialog, which) -> {
                    // Capture the rating
                    Float rating = ratingBar.getRating();
                    try {
                        // send rating
                        postCourseRating(course, rating);
                    } catch (Exception e) {
                        Log.d("ExceptionSendMessage:", e.getMessage().toString());
                    }
                    // update the rating
                });
            }

        }

        courseDialog = builder.create();

        courseDialog.show();
    }

    @Override
    public void onWebSocketMessage(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("Websocket Message", message);
                try {
                    JSONObject jsonObject = new JSONObject(message);

                    // Check if the JSON object has the key "comment"
                    if (jsonObject.has("comment")) {
                        String userCommentValue = jsonObject.getString("comment");

                        if (!courseComments.contains(userCommentValue)) {
                            courseComments.add(userCommentValue);
                            // brute force to show in the ui
                            commentsList.setFocusable(true);
                            commentsList.setFocusableInTouchMode(true);
                            commentsList.requestFocus();
                            courseDialog.dismiss();
                            courseDialog.show();
                        }
                    } else {
                        // otherwise it's the rating
                        String rating = jsonObject.getString("rating");
                        Float ratingFloat = Float.parseFloat(rating);
                        ratingBar.setRating(ratingFloat);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * add an empty semester to the semester array
     * @param view
     */
    public void addSemester(View view){
        // Create an empty ArrayList
        List<String> emptyArrayList = new ArrayList<>();
        semesterList.add(emptyArrayList);
        // generate the schedule with the new semester
        semesterContainer.removeAllViews();
        populateSchedule(semesterList);
    }


    /**
     * set up the ability to drag a course to a different semester
     * @param courseView
     * @param course
     */
    private void setupDrag(View courseView, String course) {
        if (isEditable) {
            courseView.setOnLongClickListener(v -> {
                ClipData data = ClipData.newPlainText("course", course);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(courseView);
                courseView.startDragAndDrop(data, shadowBuilder, courseView, 0);
                return true;
            });
        } else {
            courseView.setOnLongClickListener(null); // Disable dragging
            // Don't need to disable drop because if you can't drag then you can't drop
        }
    }


    /**
     * set up the ability to drop a course into a different semester
     * @param semesterView
     * @param semesterNumber
     */
    private void setupDrop(View semesterView, int semesterNumber) {
        semesterView.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DROP:
                    // Get the course from the dragged data
                    String course = event.getClipData().getItemAt(0).getText().toString();

                    // Update the data model
                    moveCourseToSemester(course, semesterNumber);

                    // Refresh the view
                    semesterContainer.removeAllViews();
                    populateSchedule(semesterList);

                    // check prereqs if it is not a course cluster
                    if (!courseClusters.containsKey(course)){
                        checkPreReqs();
                    }

                    return true;
                default:
                    return true;
            }
        });
    }

    /**
     * Update the Schedule List of the student after they move a course
     * @param course
     * @param semesterNumber
     */
    private void moveCourseToSemester(String course, int semesterNumber) {
        // Remove the course from its current semester
        for (List<String> semester : semesterList) {
            if (semester.contains(course)) {
                semester.remove(course);
                break;
            }
        }

        // Add the course to the new semester
        semesterList.get(semesterNumber).add(course);
    }

    /**
     * take the user to the saved plans
     * @param view
     */
    public void goToSavedPlans(View view) {
        sessionManagement.setPlanOwnerID(user_id);
        Intent intent = new Intent(HomeActivity.this, SavedPlansActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * go to the menu on click
     *
     * @param view
     */
    public void goToMenu(View view) {
        Intent intent = new Intent(HomeActivity.this, DropDownMenu.class);
        startActivity(intent);
        finish();
    }

    /**
     * call the PUT request method
     * @param view
     */
    public void savePlan(View view){
        if (!courseClusters.isEmpty()){
            Toast.makeText(HomeActivity.this, "Make Course Selections Before Saving Plans", Toast.LENGTH_LONG).show();
        } else {
            plan_name = schedule_name.getText().toString();
            createUserSchedule();
            goToSavedPlans(null);
        }
    }

    /**
     * set the plan maker as the default plan for the users major
     * @param view
     */
    public void getDefaultPlan(View view){
        semesterContainer.removeAllViews();
        getDefaultSchedule();
    }

    /**
     * parse the default plan with the course clusters
     * set up a map with the course clusters for later use in the pop up menu
     * @param jsonResponse JSONObject of the default schedule with course clusters
     */
    private void getDefaultPlanHelper(JSONObject jsonResponse) throws JSONException {
        JSONArray classesArray = jsonResponse.getJSONArray("classes");
        JSONArray clustersArray = jsonResponse.getJSONArray("clusters");

        // process classes and clusters
        generateArrayFromJson(classesArray);
        processClusters(clustersArray);
    }

    private void processClusters(JSONArray clusters) throws JSONException {
        int index = 1;
        for (int i = 0; i < clusters.length(); i++) {
            JSONObject cluster = clusters.getJSONObject(i);
            String title = cluster.getString("title");
            int semester = cluster.getInt("semester");

            JSONArray classes = cluster.getJSONArray("classes");
            List<String> classList = new ArrayList<>();
            for (int j = 0; j < classes.length(); j++) {
                classList.add(classes.getString(j));
            }

            // put the cluster and list of classes in the map
            if (courseClusters.containsKey(title)){
                title = title + " " + String.valueOf(index++);
                courseClusters.put(title, classList);
            } else {
                index = 1;
                courseClusters.put(title, classList);
            }

            // put the cluster name in the schedule
            while (semesterList.size() <= semester) {
                semesterList.add(new ArrayList<>());
            }
            semesterList.get(semester).add(title);
        }
        Log.d("Clusters Map", courseClusters.toString());
    }

    private void alertPrereqFail(String[] alertCourses){
        semesterContainer.removeAllViews();
        populateSchedule(semesterList, alertCourses);
    }

    /**
     * helper to generate the schedule array from the GET request
     * @param jsonArray
     * @throws JSONException
     */
    private void generateArrayFromJson(JSONArray jsonArray) throws JSONException {
        Map<Integer, List<String>> semesterMap = new TreeMap<>();  // TreeMap to keep semesters in order
        semesterMap.put(0, new ArrayList<>());
        for (int i = 0; i < jsonArray.length(); i++) {
            String courseObj = jsonArray.getString(i);  // Get the item in the array
            String[] parts = courseObj.split(","); // split the string into course code and semester

            // Check if parts has at least 2 elements
            if (parts.length < 2) {
                continue;  // Skip if not
            }

            String courseCode = parts[0].trim();
            int semester = Integer.parseInt(parts[1].trim());

            if (!semesterMap.containsKey(semester)) {
                semesterMap.put(semester, new ArrayList<>());
            }
            semesterMap.get(semester).add(courseCode);

        }
        semesterList = new ArrayList<>(semesterMap.values());
        Log.d("SemesterList", semesterList.toString());
    }

    /**
     * puts the semester list into the correct format for the POST request
     * @param semesters
     * @return formatted list of Strings
     */
    private String[] POSTRequestHelper(List<List<String>> semesters){
        List<String> resultList = new ArrayList<>();

        for (int i = 0; i < semesters.size(); i++) {
            List<String> semester = semesters.get(i);
            for (String className : semester) {
                resultList.add(className + ", " + (i));
            }
        }

        return resultList.toArray(new String[0]);
    }

    /**
     * POST request a new course comment
     */
    public void postCourseComment(String course, String comment){
        url_post_comment = url_base + "/addCourseComment";
        //String request object
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_post_comment, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Send the comment via WebSocket
                WebSocketManager.getInstance().sendMessage("comment", comment);
                Toast.makeText(HomeActivity.this, "Comment Posted", Toast.LENGTH_LONG).show();
            }
            },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, "Cannot post course comment", Toast.LENGTH_LONG).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("commentInput", comment);
                params.put("courseCode", course);
                return params;
            }

        };//End of String Req Object
        queue.add(stringRequest);
    }

    /**
     * GET request the course comments
     */
    public void getCourseComments(String course){
        courseCode = course;
        url_get_comments = url_base + "/getCourseComments?courseCode=" + courseCode;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_get_comments,
                response -> {
                    Log.d("GET Response", response);
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            courseComments.add(jsonArray.getString(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getCourseRating(course);

                },
                error -> Toast.makeText(HomeActivity.this, "Cannot find course comments", Toast.LENGTH_LONG).show()
        );

        // Add the request to the RequestQueue
        queue.add(stringRequest);
    }

    /**
     * post the course rating
     * @param course
     * @param rating
     */
    public void postCourseRating(String course, Float rating){
        String url_put_rating = url_base + "/addCourseRating";
        //String request object
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url_put_rating, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(HomeActivity.this, "Rating Posted", Toast.LENGTH_LONG).show();
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, "Cannot post course rating", Toast.LENGTH_LONG).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("courseCode", course);
                params.put("userRating", String.valueOf(rating));
                return params;
            }

        };
        queue.add(stringRequest);
    }

    /**
     * allow advisor to reset the course ratings
     * @param course
     */
    public void resetCourseRating(String course){
        String url_reset_rating = url_base + "/resetCourseRating";
        //String request object
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url_reset_rating, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(HomeActivity.this, "Rating Reset", Toast.LENGTH_LONG).show();
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, "Cannot reset course rating", Toast.LENGTH_LONG).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("courseCode", course);
                return params;
            }

        };
        queue.add(stringRequest);
    }

    /**
     * GET request the course comments
     */
    public void getCourseRating(String course){
        courseCode = course;
        url_get_comments = url_base + "/getCourseRating?courseCode=" + courseCode;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_get_comments,
                response -> {
                    String ratingString = response;
                    if (ratingString == null){
                        courseRating = 0.0f;
                    }
                    Float rating = Float.parseFloat(ratingString);
                    courseRating = rating;
                    try {
                        showCoursePopup(courseCode);
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> Toast.makeText(HomeActivity.this, "Cannot find course rating", Toast.LENGTH_LONG).show()
        );

        // Add the request to the RequestQueue
        queue.add(stringRequest);
    }


    /**
     * GET request the course description
     */
    public void getCourseDescription(String course){
        // make a GET request
        courseCode = course;
        url_get_description = url_base + "/getCourseDescription?courseCode=" + courseCode;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_get_description,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("GET Response", response);
                        getCourseComments(courseCode);
                        course_details = response;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeActivity.this, "Cannot find course description", Toast.LENGTH_LONG).show();
                    }
                }
        );

        // Add the request to the RequestQueue
        queue.add(stringRequest);
    }

    /**
     * GET request the default schedule for the user major
     */
    public void getDefaultSchedule(){
        // make a GET request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_get_plan_default,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("GET Response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            getDefaultPlanHelper(jsonObject);
                            //generateArrayFromJson(jsonArray);
                            populateSchedule(semesterList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(HomeActivity.this, "Error parsing user schedule", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeActivity.this, "Cannot find user default schedule", Toast.LENGTH_LONG).show();
                    }
                }
        );

        // Add the request to the RequestQueue
        queue.add(stringRequest);
    }

    /**
     * GET request schedule by schedule id for student
     */
    public void getUserSchedule(){
        if (isEditable){
            url_get_plan = url_base + "?userID=" + user_id + "&planName=" + plan_name;
        } else {
            url_get_plan = url_base + "?userID=" + plan_owner_id + "&planName=" + plan_name;
        }
        // make a GET request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_get_plan,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("GET Response", response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            generateArrayFromJson(jsonArray);
                            populateSchedule(semesterList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(HomeActivity.this, "Error parsing user schedule", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeActivity.this, "Cannot find user schedule", Toast.LENGTH_LONG).show();
                    }
                }
        );

        // Add the request to the RequestQueue
        queue.add(stringRequest);
    }

    /**
     * PUT request to update student schedule
     * in the backend it will either create a new schedule or
     * update the schedule if it exists
     */
    public void createUserSchedule(){
        // make a POST request

        // convert classes to array format
        // ex. ["class 1, 1", "class 2, 2"] where the first item is the class and the second is the
        // semester in which it is taken
        String[] classes = POSTRequestHelper(semesterList);

        // create JSON object to send over
        JSONObject json = new JSONObject();
        try {
            json.put("userID", user_id);
            json.put("planName", plan_name);
            json.put("classes", new JSONArray(Arrays.asList(classes)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("JSON Object", json.toString());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url_post, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the response here
                        Log.d("Post Response", response.toString());
                        Toast.makeText(HomeActivity.this, "Plan Updated", Toast.LENGTH_LONG).show();
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

        queue.add(jsonRequest);
    }

    public String getAlertMessage(){
        if (alertMessage == null){
            return "Alert Message is Null";
        } else {
            return alertMessage;
        }
    }

    public void setAlertMessage(String message){
        alertMessage = message;
    }
    /**
     * GET request to check the prereqs
     */
    public void checkPreReqs(){
        String url_get_prereqs = url_base + "/checkPrereqsAndCoreqs";
        // make a GET request

        // convert classes to array format
        // ex. ["class 1, 1", "class 2, 2"] where the first item is the class and the second is the
        // semester in which it is taken
        String[] classes = POSTRequestHelper(semesterList);

        // create JSON object to send over
        JSONObject json = new JSONObject();
        try {
            json.put("classes", new JSONArray(Arrays.asList(classes)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("JSON Object", json.toString());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, url_get_prereqs, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("GET Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String responseBody = new String(error.networkResponse.data);
                            JSONObject jsonObject = null;
                            String messageValue = "";
                            try {
                                jsonObject = new JSONObject(responseBody);
                                alertMessage = jsonObject.getString("message");
                                setAlertMessage(jsonObject.getString("message"));
                                Log.d("Alert Message", alertMessage);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            try {
                                messageValue = jsonObject.getString("Reason");
                                try {
                                    JSONArray alertCoursesJsonArray = jsonObject.getJSONArray("alertCourses");
                                    String[] alertCourses = new String[alertCoursesJsonArray.length()];


                                    for (int i = 0; i < alertCoursesJsonArray.length(); i++) {
                                        alertCourses[i] = alertCoursesJsonArray.getString(i);
                                    }

                                    alertPrereqFail(alertCourses);
                                } catch (JSONException e){
                                    Log.d("JSON Error", "Alert Courses Error");
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            Log.d("Error.Response Body", responseBody);
                            //Toast.makeText(HomeActivity.this, messageValue, Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("Error.Response String", error.toString());
                        }
                    }
                }
        );

        queue.add(jsonRequest);
    }

    /**
     * make a get request to search for the course
     */
    public void searchForCourse(String course){
        // make a GET request
        String url_search_course = url_base + "/course?courseID=" + course;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_search_course, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("GET Response", response);
                if (response.trim().equals("1")) {
                    if (semesterList.isEmpty()) {
                        // If empty, add a new ArrayList to it
                        semesterList.add(new ArrayList<>());
                    }
                    // Get the first semester from semesterList
                    List<String> firstSemester = semesterList.get(0);
                    // Add the new string to the first semester
                    firstSemester.add(course);
                    // regenerate so it's visible
                    semesterContainer.removeAllViews();
                    populateSchedule(semesterList);
                    Toast.makeText(HomeActivity.this, "Course Added to Semester 0", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(HomeActivity.this, "Cannot find course", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, "Cannot find course", Toast.LENGTH_LONG).show();
            }
        }); //End of String Req Object
        queue.add(stringRequest);
    }

    private void getAdvisorComments() {
        url_chats += "/advisor/homepage/getChats?adviseeEmail="  ;
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {

    }


    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onWebSocketError(Exception ex) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebSocketManager.getInstance().disconnectAllWebSockets();
    }

    @Override
    protected void onPause() {
        super.onPause();
        WebSocketManager.getInstance().disconnectAllWebSockets();
    }

    public void postCommentForAdvisor(View view) {
        String url_postCommentForAdvisor = ip + "/advisor/homepage/sendChat";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_postCommentForAdvisor, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equalsIgnoreCase(" Successful")) {
                    Toast.makeText(HomeActivity.this, "Comment posted", Toast.LENGTH_LONG).show();
                }
                //End of response if block
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError", "Error: " + error.toString());
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //post the information to SB
                params.put("advisorEmail", sessionManagement.getUserEmail());
                params.put("adviseeEmail", sessionManagement.getPlanOwnerEmail());
                params.put("chat", advisorCommentSection.getText().toString());
                params.put("plan_name", schedule_name.getText().toString());
                return params;
            }

        };//End of String Req Object
        queue.add(stringRequest);
    }

    /**
     * GET request the plan comments
     */
    public void getPlanComments(){
        String url_get_plan_comments = ip + "/advisor/homepage/getChats?adviseeEmail=" + sessionManagement.getPlanOwnerEmail() + "&plan_name=" + schedule_name.getText().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_get_plan_comments,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("JSON object", jsonObject.toString());
                            String jSONmessage = jsonObject.getString("Message");
                            JSONArray jsonArray = new JSONArray(jSONmessage);

                            String[] comments = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String comment = jsonArray.getString(i);
                                comment = comment.replace("\"", ""); // Removes all double quotes
                                comment = comment.replace("[", "");  // Removes all opening square brackets
                                comment = comment.replace("]", "");  // Removes all closing square brackets
                                comments[i] = comment;
                            }
                            // Create and set the adapter
                            ArrayAdapter<String> commentsAdapter2 = new ArrayAdapter<>(
                                    HomeActivity.this,
                                    android.R.layout.simple_list_item_1,
                                    comments
                            );
                            ListView commentsListView = findViewById(R.id.comments_list);
                            commentsListView.setAdapter(commentsAdapter2);
                            getUserSchedule();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                        Log.d("GET Response", response);
                        getUserSchedule();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        getUserSchedule();
                    }
                }
        );

        // Add the request to the RequestQueue
        queue.add(stringRequest);
    }
}