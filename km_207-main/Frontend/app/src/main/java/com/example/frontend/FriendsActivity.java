package com.example.frontend;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.frontend.helpers.GlobalVariableHelper;
import com.example.frontend.helpers.SessionManagement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * This activity allows users to lookup other users by email and
 * add them as a friend.
 */
public class FriendsActivity extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchButton, addFriend, backButton;
    TextView textViewReceivedName, textViewReceivedMajor, textViewReceivedEmail;
    String searchTerm, url_get, myEmail, friendEmail;
    RequestQueue queue;
    SessionManagement sessionManagement;
    String ip = GlobalVariableHelper.ip;

    // Replace with your IP to test
    String url = ip + "/friends/addFriend";

    /**
     * Declares values for all of the things on the screen.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        sessionManagement = new SessionManagement(FriendsActivity.this);

        // Hook buttons
        searchInput = findViewById(R.id.search_input);
        searchButton = findViewById(R.id.search_user_btn);
//        addFriend = findViewById(R.id.imageButtonAddFriend);
        backButton = findViewById(R.id.backButton);

        // Hook textView
        textViewReceivedName = findViewById(R.id.textViewReceivedName);
        textViewReceivedMajor = findViewById(R.id.textViewReceivedMajor);
        textViewReceivedEmail = findViewById(R.id.textViewReceivedEmail);

        // Automatically pulls the keyboard up and puts the cursor on the EditText.
        searchInput.requestFocus();

        queue = Volley.newRequestQueue(FriendsActivity.this);
    }

    /**
     * Sets the search term to text the user has entered, then
     * sends a search request.
     * Method is called when user clicks the search icon.
     * @param view
     */
    public void search(View view) {
        // Sets the String equal to the text entered by the user
        searchTerm = searchInput.getText().toString();
        searchRequest();
    }

    /**
     * Uses a GET method to receive information from the back end on the
     * user entered in the search field.
     */
    public void searchRequest() {
        // Server URL
         url_get = ip + "/profile/getCred?email=" + searchTerm;

        // make a GET request to the above url
        StringRequest getRequest = new StringRequest(Request.Method.GET, url_get, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) { // RESPONSE MUST BE A STRING FOR THIS TO WORK AS WRITTEN

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String jSONname = jsonObject.getString("first_name");
                    String jSONmajor = jsonObject.getString("major");

                    // Set the textView to the name being returned from API
                    textViewReceivedName.setText(jSONname);
                    textViewReceivedMajor.setText(jSONmajor);
                    textViewReceivedEmail.setText(searchTerm);

//                    Toast.makeText(FriendsActivity.this, "User displayed successfully", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(FriendsActivity.this, "Error parsing user information", Toast.LENGTH_LONG).show();
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Set the textView to the String of prereqs being returned from API
                textViewReceivedName.setText("No users found");
                textViewReceivedMajor.setText("");
                textViewReceivedEmail.setText("");
//                Toast.makeText(FriendsActivity.this, "No users found", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(getRequest);
    }

    /**
     * Adds the user to the list of friends for the logged-in user.
     * Method is called when imageButtonAddFriend is clicked.
     */
    public void previewFriend(View view) {

        // a valid user was not found
        if (textViewReceivedName.getText().toString().equals("No users found")) {
            Toast.makeText(FriendsActivity.this, "User does not exist", Toast.LENGTH_LONG).show();
        }
        // a valid user was found
        else {
//            Toast.makeText(FriendsActivity.this, "Added friend", Toast.LENGTH_LONG).show();
            getUserInfo(searchTerm);
        }
    }

    /**
     * Takes the user back to the list of friends.
     * Method is called when backButton is pressed.
     */
    public void backToChats(View view) {
        if (sessionManagement.getUserType().equals("A")) {
            startActivity(new Intent(FriendsActivity.this, DropDownMenu.class));
        } else startActivity(new Intent(FriendsActivity.this, AllChatsActivity.class));
    }

    public void getUserInfo(String friendEmail){
        // Server URL
        String url_get = ip + "/friends/getUserInfo?email=" + friendEmail;

        // make a GET request to the above url
        StringRequest getRequest = new StringRequest(Request.Method.GET, url_get, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) { // RESPONSE MUST BE A STRING FOR THIS TO WORK AS WRITTEN

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String jSONname = jsonObject.getString("name");
                    String jSONmajor = jsonObject.getString("major");
                    int jSONid = jsonObject.getInt("userID");

                    // Set the textView to the name being returned from API
                    sessionManagement.setPlanOwnerName(jSONname);
                    sessionManagement.setPlanOwnerID(jSONid);
                    sessionManagement.setPlanOwnerMajor(jSONmajor);
                    sessionManagement.setPlanOwnerEmail(friendEmail);

                    goToTheirAccount(null);

//                    Toast.makeText(FriendsActivity.this, "User displayed successfully", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(FriendsActivity.this, "Error parsing user information", Toast.LENGTH_LONG).show();
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Set the textView to the String of prereqs being returned from API
                Toast.makeText(FriendsActivity.this, "No users found", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(getRequest);
    }

    private void goToTheirAccount(View view) {
        GlobalVariableHelper.lastActivity = "";
        startActivity(new Intent(FriendsActivity.this, FriendAccountActivity.class));
    }
}