package com.example.frontend;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.example.frontend.helpers.websockets.WebSocketListener;
import com.example.frontend.helpers.websockets.WebSocketManager;
import com.example.frontend.recyclerHelpers.Item;
import com.example.frontend.recyclerHelpers.MyAdapter;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This activity shows all the friends that a user has added.
 */
public class AllChatsActivity extends AppCompatActivity{

    LinearLayout friendsList, friendRequestsList;
    String url_get, url_getRequests, url_acceptRequest, userEmail, userName, friendEmail;
    RequestQueue queue;
    SessionManagement sessionManagement;
    TextView title;
    String ip = GlobalVariableHelper.ip;
    String url_remove = ip + "/friends/deleteFriend";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);

        sessionManagement = new SessionManagement(AllChatsActivity.this);
        userEmail = sessionManagement.getUserEmail();
        userName = sessionManagement.getUserName();

        title = findViewById(R.id.textViewTitle);
        title.setText(userName + "'s Friends");

        // Initialize friendsList layout
        friendsList = findViewById(R.id.friendsList);

        // Initialize friendRequests layout
        friendRequestsList = findViewById(R.id.friendRequestsList);

        displayFriendsAndRequests();
    }

    private void displayFriendsAndRequests() {
        friendsList.removeAllViews();
        friendRequestsList.removeAllViews();
        // Load and display the list of friend requests
        displayFriendRequests();

        // Load and display the list of friends
        displayFriends();
    }

    private View createFriendView(Item item) {
        // Inflate the friend_item.xml layout
        View friendView = getLayoutInflater().inflate(R.layout.search_user_recycler_row, null);

        // Find and set the views' content based on the friend's information
        TextView userName = friendView.findViewById(R.id.user_email_text);

        userName.setText(item.getEmail()); // Set the user's name

        return friendView;
    }

    private View createFriendRequestView(Item item) {
        // Inflate your friend request layout
        View requestView = getLayoutInflater().inflate(R.layout.friend_request_row, null);

        // Find and set the views
        TextView requesterName = requestView.findViewById(R.id.requesterName);
        Button acceptButton = requestView.findViewById(R.id.acceptButton);
        Button rejectButton = requestView.findViewById(R.id.rejectButton);

        requesterName.setText(item.getEmail());

        // Set click listeners for accept and reject buttons
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptFriendRequest(item.getEmail());
            }
        });
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rejectFriendRequest();
                displayFriendsAndRequests();
            }
        });

        return requestView;
    }

    private void displayFriends() {
        // Fetch friends from the data source
        getFriendsFromDataSource(new VolleyCallback() {
            @Override
            public void onSuccess(List<Item> items) {
                for (Item friend : items) {
                    // Create a TextView for each friend
                    View friendView = createFriendView(friend);

                    // Set an OnClickListener to go to their profile
                    friendView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            friendEmail = friend.getEmail();
                            getUserInfo(friendEmail);
                        }
                    });

                    // Add the TextView to the friendsList layout
                    friendsList.addView(friendView);
                }
            }
        });
    }

    private void displayFriendRequests() {
        // Fetch friend requests from the data source
        getFriendRequestsFromDataSource(new VolleyCallback() {
            @Override
            public void onSuccess(List<Item> items) {
                for (Item friend : items) {
                    // Create a TextView for each friend
                    View friendRequestView = createFriendRequestView(friend);

                    // Add the TextView to the friendsList layout
                    friendRequestsList.addView(friendRequestView);
                }
            }
        });
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

//                    Toast.makeText(AllChatsActivity.this, "User displayed successfully", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(AllChatsActivity.this, "Error parsing user information", Toast.LENGTH_LONG).show();
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Set the textView to the String of prereqs being returned from API
//                Toast.makeText(AllChatsActivity.this, "No users found", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(getRequest);
    }

    private void goToTheirAccount(View view) {
        GlobalVariableHelper.lastActivity = "AllChatsActivity";
        startActivity(new Intent(AllChatsActivity.this, FriendAccountActivity.class));
    }

//    private void openChatWithFriend(Item friend) {
//        // Websocket stuff
//        String websocket_url = websocket_url_base + friend.getEmail();
//
//        // Set up the view
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = getLayoutInflater();
//        View view = inflater.inflate(R.layout.text_screen, null);
//
//        // Get views from the layout
//        TextView chatTitle = view.findViewById(R.id.chat_title);
//        ListView chatMessagesList = view.findViewById(R.id.chat_messages_list);
//        EditText messageInput = view.findViewById(R.id.message_input);
//
//        // Establish WebSocket connection and set listener
//        WebSocketManager.getInstance().disconnectWebSocket("chatSocket");
//        WebSocketManager.getInstance().connectWebSocket("chatSocket", websocket_url);
//        WebSocketManager.getInstance().setWebSocketListener(AllChatsActivity.this);
//
//        // used for "Chat with " + email
//        chatTitle.setText("Chat with " + friend.getEmail());
//
//        // Instantiate the adapter for chat messages
//        chatAdapter = new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_list_item_1,
//                chats
//        );
//        chatMessagesList.setAdapter(chatAdapter);
//
//        builder.setView(view);
//
//        // Handle sending chat messages via WebSocket on OK button click
//        builder.setPositiveButton("SEND", (dialog, which) -> {
//            // Capture the message from the input
//            String message = messageInput.getText().toString().trim();
//            if (!message.isEmpty()) {
//                try {
//                    WebSocketManager.getInstance().sendMessage("chatSocket", message);
//                    chatAdapter.notifyDataSetChanged();
//                } catch (Exception e) {
//                    Log.d("ExceptionSendMessage:", e.getMessage().toString());
//                }
//            }
//            messageInput.setText("");
//        });
//
//        // on click of "Exit", dismiss and disconnect
//        builder.setNegativeButton("Exit", (dialog, which) -> {
//            chatDialog.dismiss();
//            chats.clear();
//            WebSocketManager.getInstance().disconnectWebSocket("chatSocket");
//        });
//
//        // Create the chat dialog and show it
//        chatDialog = builder.create();
//        chatDialog.show();
//    }

    /**
     * Fetches friends from the backend.
     * @return
     */
    private void getFriendsFromDataSource(final VolleyCallback callback) {
        userEmail = sessionManagement.getUserEmail();
        queue = Volley.newRequestQueue(AllChatsActivity.this);
        url_get = ip + "/friends/showFriends?email=" + userEmail; //

        StringRequest getRequest = new StringRequest(Request.Method.GET, url_get, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    List<Item> items = new ArrayList<>();

                    // Parse the JSON response
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray friendsArray = jsonObject.getJSONArray("friends");

                    for (int i = 0; i < friendsArray.length(); i++) {
                        String friendEmail = friendsArray.getString(i);
                        // Create an Item object using the friend's email and add it to the list
                        items.add(new Item(friendEmail));
                    }

                    // Callback to inform the caller that data is ready
                    callback.onSuccess(items);
                } catch (Exception e) {
                    Log.e("VolleyError", "Error: " + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError", "Error: " + error.toString());
//                Toast.makeText(AllChatsActivity.this, "show friends error", Toast.LENGTH_LONG).show();
            }
        });

        // Add the request to the queue
        queue.add(getRequest);
    }

    private void getFriendRequestsFromDataSource(final VolleyCallback callback) {
        userEmail = sessionManagement.getUserEmail();
        queue = Volley.newRequestQueue(AllChatsActivity.this);
        url_getRequests = ip + "/friends/yourRequests?currentUserEmail=" + userEmail; //FIXME

        StringRequest getRequest = new StringRequest(Request.Method.GET, url_getRequests, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    List<Item> items = new ArrayList<>();

                    // Parse the JSON response
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray friendRequestArray = jsonObject.getJSONArray("Requested You");

                    for (int i = 0; i < friendRequestArray.length(); i++) {
                        String friendEmail = friendRequestArray.getString(i);
                        // Create an Item object using the friend's email and add it to the list
                        items.add(new Item(friendEmail));
                    }

                    // Callback to inform the caller that data is ready
                    callback.onSuccess(items);
                } catch (Exception e) {
                    Log.e("VolleyError", "Error: " + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError", "Error: " + error.toString());
//                Toast.makeText(AllChatsActivity.this, "requests error", Toast.LENGTH_LONG).show();
            }
        });

        // Add the request to the queue
        queue.add(getRequest);
    }

    /**
     * my email and friend email need to be reversed for this to work.
     * @param friendEmail
     */
    private void acceptFriendRequest(String friendEmail) {

        String myEmail = sessionManagement.getUserEmail();
        url_acceptRequest = ip + "/friends/addFriend";

        // String request object
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_acceptRequest, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                displayFriendsAndRequests();
//                Toast.makeText(AllChatsActivity.this, "accepted friend request", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(AllChatsActivity.this, "VolleyError", Toast.LENGTH_LONG).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Create a map of parameters to send with the POST request.
                Map<String, String> params = new HashMap<>();
                // Post the information to the backend.
                params.put("currentUserEmail", friendEmail);
                params.put("pendingFriendEmail", myEmail);
                return params;
            }

        };//End of String Req Object
        queue.add(stringRequest);
    }

    public void rejectFriendRequest() {

        String myEmail = sessionManagement.getUserEmail();
        String friendEmail = sessionManagement.getPlanOwnerEmail();

        url_remove += "?currentUserEmail=" + myEmail + "&friendEmail=" + friendEmail;

        // String request object
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url_remove, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) { // RESPONSE MUST BE A STRING FOR THIS TO WORK AS WRITTEN
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

    interface VolleyCallback {
        void onSuccess(List<Item> items);
    }

    /**
     * Takes the user to the search screen.
     * @param view
     */
    public void goToSearch(View view) {
        startActivity(new Intent(AllChatsActivity.this, FriendsActivity.class));
    }

    /**
     * Takes the user to the DropDownMenu.
     * @param view
     */
    public void goToMenu(View view) {
        WebSocketManager.getInstance().disconnectWebSocket("statusSocket");
        startActivity(new Intent(AllChatsActivity.this, DropDownMenu.class));
    }
}
