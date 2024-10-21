package com.example.frontend;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class FriendAccountActivity extends AppCompatActivity {

    SessionManagement sessionManagement;
    Button menu, plans;
    RequestQueue queue;
    TextView name, major, email, addFriend;
    String ip = GlobalVariableHelper.ip;
    String friendViewing = GlobalVariableHelper.friendViewing;
    String url = ip + "/friends/requestFriend";
    String url_remove = ip + "/friends/deleteFriend";
    boolean friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_account);

        sessionManagement = new SessionManagement(FriendAccountActivity.this);

        menu = findViewById(R.id.menuButton);
        name = findViewById(R.id.nameTextView);
        major = findViewById(R.id.majorTextView);
        email = findViewById(R.id.emailTextView);
        addFriend = findViewById(R.id.addFriendTextView);
        plans = findViewById(R.id.plansButton);

        queue = Volley.newRequestQueue(FriendAccountActivity.this);

        //Check if they are friends
        String last = GlobalVariableHelper.lastActivity;
        if (last.equals("AllChatsActivity")) {
            friends = true;
        } else friends = false;

        //Advisor setup
        if (sessionManagement.getUserType().equals("A")) {
            friends = true;
            addFriend.setVisibility(View.GONE);
        }

        //Set the friend textview to display "+ Add friend" or disappear
        if (friends) {
            addFriend.setText("- Remove friend");
        }
        else {
            addFriend.setText("+ Add friend");
        }

        name.setText(sessionManagement.getPlanOwnerName());
        major.setText(sessionManagement.getPlanOwnerMajor());
        email.setText(sessionManagement.getPlanOwnerEmail());
    }

    public void goToMenu(View view) {
        startActivity(new Intent(FriendAccountActivity.this, DropDownMenu.class));
    }

    /**
     * Method is called when imageButtonAddFriend is clicked.
     */
    public void changeFriendStatus(View view) {
        if (friends) {
            removeFriend();
        }
        else {
            addFriend();
        }
    }
    public void addFriend() {

        SessionManagement sessionManagement = new SessionManagement(FriendAccountActivity.this);

        String myEmail = sessionManagement.getUserEmail();
        String friendEmail = friendViewing;

        // String request object
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Toast.makeText(FriendAccountActivity.this, response, Toast.LENGTH_LONG).show();
                addFriend.setVisibility(View.GONE);
                Log.d("Add Friend", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(FriendAccountActivity.this, "VolleyError", Toast.LENGTH_LONG).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Create a map of parameters to send with the POST request.
                Map<String, String> params = new HashMap<>();
                // Post the information to the backend.
                params.put("currentUserEmail", myEmail);
                params.put("pendingFriendEmail", sessionManagement.getPlanOwnerEmail());
                return params;
            }

        };//End of String Req Object
        queue.add(stringRequest);
    }

    public void removeFriend() {

        SessionManagement sessionManagement = new SessionManagement(FriendAccountActivity.this);

        String myEmail = sessionManagement.getUserEmail();
        String friendEmail = sessionManagement.getPlanOwnerEmail();

        url_remove += "?currentUserEmail=" + myEmail + "&friendEmail=" + friendEmail;

        // String request object
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url_remove, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) { // RESPONSE MUST BE A STRING FOR THIS TO WORK AS WRITTEN
                friends = false;
                addFriend.setText("+ Add friend");
//                Toast.makeText(FriendAccountActivity.this, "friend deleted", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(FriendAccountActivity.this, "error for friend deleted", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }

    public void goToTheirPlans(View view) {
        if (friends) {
            startActivity(new Intent(FriendAccountActivity.this, SavedPlansActivity.class));
        }
        else {
            Toast.makeText(FriendAccountActivity.this, "You must be friends to view their plans.", Toast.LENGTH_LONG).show();
        }
    }
}