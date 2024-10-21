package com.example.frontend;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.example.frontend.helpers.SpinnerHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * profile activity for student user
 */
public class ProfileActivity extends AppCompatActivity {

    String ip = GlobalVariableHelper.ip;
    Button saved_plans, menu, logOut, changePassword, deleteAccount;
    ImageView edit_profile;

    EditText name;

    TextView major, email;

    Spinner major_spinner;

    String selected_major, user_name, userType;

    boolean edit_mode = false;

    RequestQueue queue;
    SessionManagement sessionManagement;

    String user_email;
    String url_put;

    String url_delete;

    /**
     * Declares values for all of the things on the screen.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edit_profile = findViewById(R.id.editProfileButton);
        saved_plans = findViewById(R.id.goToSavedPlansButton);
        name = findViewById(R.id.nameEditText);
        major = findViewById(R.id.majorTextView);
        major_spinner = findViewById(R.id.majorSpinner);
        email = findViewById(R.id.emailTextView);
        logOut = findViewById(R.id.logOutButton);
        changePassword = findViewById(R.id.changePasswordButton);
        deleteAccount = findViewById(R.id.deleteAccountButton);

        menu = findViewById(R.id.menuButton);

        //Instantiate the request queue
        queue = Volley.newRequestQueue(ProfileActivity.this);

        sessionManagement = new SessionManagement(ProfileActivity.this);
        userType = sessionManagement.getUserType();
        user_email = sessionManagement.getUserEmail();

        update();

        // Create an Spinner using the helper
        ArrayAdapter<CharSequence> adapter = SpinnerHelper.createSimpleSpinner(this, R.array.majors_array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        major_spinner.setAdapter(adapter);
        selected_major = major_spinner.getSelectedItem().toString();

        // set the user info
//        getUserInformation();
    }

    public void update() {
        email.setText(sessionManagement.getUserEmail());
        name.setText(sessionManagement.getUserName());
        major.setText(sessionManagement.getUserMajor());
    }

    /**
     * takes the user to the drop down menu
     * @param view
     */
    public void goToMenu(View view) {
        Intent intent = new Intent(ProfileActivity.this, DropDownMenu.class);
        startActivity(intent);
        finish();
    }

    /**
     * takes the user to their saved plans
     * @param view
     */
    public void goToSavedActivities(View view){
        Intent intent = new Intent(ProfileActivity.this, SavedPlansActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * calls the DELETE request to delete the user account
     * @param view
     */
    public void deleteAccount(View view){
        deleteAccountRequest();
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * logs the user out
     * @param view
     */
    public void logOut(View view){
        SessionManagement sessionManagement = new SessionManagement(ProfileActivity.this);
        sessionManagement.removeSession();
        // implement clearing the cache
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * switches between the edit mode
     * @param view
     */
    public void modeSwitch(View view){
        if (edit_mode) { // edit mode is true
            selected_major = major_spinner.getSelectedItem().toString();
            major.setText(selected_major);
            user_name = name.getText().toString();
            saveChanges();
            sessionManagement.setUserName(name.getText().toString());
            sessionManagement.setUserMajor(major.getText().toString());
            editMajor(false);
            editName(false);
            showButtons(false);
//            edit_profile.setText("Edit Profile");
            edit_mode = false;
        } else { // edit mode is false
            editMajor(true);
            editName(true);
            showButtons(true);
//            edit_profile.setText("Save Changes");
            edit_mode = true;
        }
    }

    private void showButtons(boolean val) {
        if (val) {
            logOut.setVisibility(View.VISIBLE);
            changePassword.setVisibility(View.VISIBLE);
            deleteAccount.setVisibility(View.VISIBLE);
            saved_plans.setVisibility(View.GONE);
        } else {
            saved_plans.setVisibility(View.VISIBLE);
            logOut.setVisibility(View.GONE);
            changePassword.setVisibility(View.GONE);
            deleteAccount.setVisibility(View.GONE);
        }
    }

    /**
     * allows the user to edit their major when in edit mode
     * @param edit_mode
     */
    public void editMajor(boolean edit_mode) {
        if (edit_mode) {
            major.setVisibility(View.GONE);
            major_spinner.setVisibility(View.VISIBLE);
            // Set the selected item based on the user's major
            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) major_spinner.getAdapter();
            int position = adapter.getPosition(selected_major);
            if (position != -1) { // If the major is found in the list
                major_spinner.setSelection(position);
            }
        } else {
            major.setVisibility(View.VISIBLE);
            major_spinner.setVisibility(View.GONE);
        }
    }

    /**
     * allows the user to edit their name when in edit mode
     * @param edit_mode
     */
    public void editName(boolean edit_mode){
        if (edit_mode){
            name.setInputType(InputType.TYPE_CLASS_TEXT);
            name.setFocusable(true);
            name.setFocusableInTouchMode(true);
            name.requestFocus();
        } else {
            name.setInputType(InputType.TYPE_NULL);
            name.setFocusable(false);
            name.setFocusableInTouchMode(false);
            // set the user_name to their name
            user_name = name.getText().toString();
        }
    }

    /**
     * DELETE request to delete user account
     */
    public void deleteAccountRequest(){
        url_delete = ip + "/profile/delete?email=" + user_email;
        // make a DELETE request
        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, url_delete,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response here
//                        getUserInformation();
                        Log.d("DELETE Response", response);
                        Toast.makeText(ProfileActivity.this, "Account Deleted", Toast.LENGTH_LONG).show();
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
     * PUT request to update changes to profile
     */
    public void saveChanges(){
        url_put = ip + "/profile/update" + user_email;
        // make the PUT request
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url_put,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response here
//                        getUserInformation();
                        Log.d("Post Response", response);
                        Toast.makeText(ProfileActivity.this, "Update Successful!", Toast.LENGTH_LONG).show();
                        sessionManagement.setUserName(name.getText().toString());
                        sessionManagement.setUserMajor(major.getText().toString());
                        update();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error here
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email.getText().toString());
                params.put("first_name", name.getText().toString());
                params.put("major", major.getText().toString());
                return params;
            }
        };
        queue.add(putRequest);
    }

    public void goToChangePassword(View view) {
        startActivity(new Intent(ProfileActivity.this, ResetPasswordActivity.class));
    }
}