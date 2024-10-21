package com.example.frontend;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.frontend.helpers.GlobalVariableHelper;
import com.example.frontend.helpers.SessionManagement;
import com.example.frontend.helpers.StringHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button sign_up, log_in;
    EditText user_name, password;
    String email, pass;
    RequestQueue queue;
    String ip = GlobalVariableHelper.ip;
    String url = ip + "/user/loginCred";
    int userId;
    String user_type;

    private SessionManagement sessionManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sign_up = findViewById(R.id.signUpButton);
        log_in = findViewById(R.id.logInButton);
        user_name = findViewById(R.id.editEmailAddress);
        password = findViewById(R.id.editPassword);

        queue = Volley.newRequestQueue(MainActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SessionManagement sessionManagement = new SessionManagement(MainActivity.this);
        userId = sessionManagement.getUserId();
        user_type = sessionManagement.getUserType();

        // if a user is logged in
        if (userId != -1) {
            // Move to the main menu if they are a student
            if (user_type.equals("S") || user_type.equals("student")) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(MainActivity.this, DropDownMenu.class);
                startActivity(intent);
                finish();
            }
        }
    }

    /**
     * signUpButton is mapped to this method via OnClick.
     * @param view
     */
    public void goToSignUp(View view){
        Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * logInButton is mapped to this method via OnClick.
     * @param view
     */
    public void logIn(View view){
        // if the email and password fields are not empty and a ...@iastate.edu email is used
        if (validateEmail() && validatePassword()) {
            getAccount();
        }
    }

    /**
     * Checks that the email field is not empty and that a ...@iastate.edu
     * email has been used.
     * @return
     */
    public boolean validateEmail(){
        email = user_name.getText().toString();
        // check that it's not empty
        if (email.isEmpty()){
            user_name.setError("Please enter your Iowa State Email");
            return false;
            // check that it's a valid @iastate email
        } else if (!StringHelper.validateEmail(email)){
            user_name.setError("Please enter your @iastate email");
            return false;
        } else {
            user_name.setError(null);
            return true;
        }
        // check that it exists
    }

    /**
     * Checks that the password field is not empty.
     * @return
     */
    public boolean validatePassword(){
        pass = password.getText().toString();
        // check that it's not empty
        if (pass.isEmpty()){
            password.setError("Please enter your password");
            return false;
        } else return true;
        // check that the password matches the email
    }

    /**
     * Checks that user has entered a valid email and send them their password.
     * TODO
     * @param view
     */
    public void forgotPassword(View view) {
        startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));
    }

    /**
     * Connects to the backend via Volley.
     * Uses a POST method to send the user-entered username and password to the backend.
     * Receives a jSON object or failure response from the backend.
     */
    public void getAccount() {

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        Log.d("Request", "We have made the request");
        // String request object
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", "Response: " + response);
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("JSON object", jsonObject.toString());
                    String jSONname = jsonObject.getString("first_name");
                    String jSONemail = jsonObject.getString("email");
                    String jSONtype = jsonObject.getString("user_type");
                    String jSONmajor = jsonObject.getString("major");
                    int jSONid = jsonObject.getInt("user_id");

                    // use the SessionManagement class to save the user's session
                    SessionManagement sessionManagement = new SessionManagement(MainActivity.this);
                    sessionManagement.saveSession(jSONname, jSONemail, jSONtype, jSONid, jSONmajor);

                    // Move to the main menu if they are a student
                    if (jSONtype.equals("S") || jSONtype.equals("student")) {
                        sessionManagement.resetPlanOwnerID();
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(MainActivity.this, DropDownMenu.class);
                        startActivity(intent);
                        finish();
                    }

                    user_name.setText(null);
                    password.setText(null);
                    Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error parsing user information", Toast.LENGTH_LONG).show();
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("url", url);
                Toast.makeText(MainActivity.this, "Login UN-successful!", Toast.LENGTH_LONG).show();
                Log.d("Error", error.toString());
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Create a map of parameters to send with the POST request.
                Map<String, String> params = new HashMap<>();
                // Post the information to the backend.
                params.put("email", email);
                params.put("password", pass);
                return params;
            }

        };//End of String Req Object
        queue.add(stringRequest);
    }

    /**
     * Used for testing purposes to bypass the login.
     */
    public void goToTest(View view) {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}