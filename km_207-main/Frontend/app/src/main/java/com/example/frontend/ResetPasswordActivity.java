package com.example.frontend;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.frontend.helpers.GlobalVariableHelper;
import com.example.frontend.helpers.SessionManagement;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordActivity extends AppCompatActivity {

    String ip = GlobalVariableHelper.ip;
    Button passwordResetButton, backToProfileButton;

    EditText makePassword, makePassword2;
    String password, verifyPassword;
    String url = ip + "/profile/passwordChange";
    String user_email;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Hook Buttons
        passwordResetButton = findViewById(R.id.passwordResetButton);
        backToProfileButton = findViewById(R.id.backToProfileButton);

        // Hook EditText
        makePassword = findViewById(R.id.makePassword);
        makePassword2 = findViewById(R.id.makePassword2);

        SessionManagement sessionManagement = new SessionManagement(this);
        user_email = sessionManagement.getUserEmail();
    }

    /**
     * Checks whether password field is empty.
     * @return password validity.
     */
    public boolean validatePassword() {
        password = makePassword.getText().toString();

        if (password.isEmpty()) {
            makePassword.setError("Please enter a valid password");
            return false;
        } else {
            makePassword.setError(null);
            return true;
        }
    }

    /**
     * Checks whether password confirmation field is empty.
     * @return password confirmation validity
     */
    public boolean validateConfirmPassword() {
        verifyPassword = makePassword2.getText().toString();

        if (verifyPassword.isEmpty()) {
            makePassword2.setError("This field cannot be empty");
            return false;
            // check that the first password is filled in because the user should have a password to validate it
        } else if (password.isEmpty()){
            makePassword2.setError("This field cannot be empty");
            return false;
        }else {
            makePassword2.setError(null);
            return true;
        }
    }

    /**
     * Checks whether the password and password confirmation fields
     * contain identical values.
     * @return password match status
     */
    public boolean passwordMatch () {
        if (!password.equals(verifyPassword)) {
            makePassword2.setError("Passwords must match");
            return false;
        } else {
            makePassword2.setError(null);
            return true;
        }
    }

    public void sendPasswordReset(View view) {
        if (!validatePassword() || !validateConfirmPassword() || !passwordMatch() ) {
            Toast.makeText(ResetPasswordActivity.this, "Fail", Toast.LENGTH_SHORT).show();
        } else {
            resetPassword();
        }
    }

    public void resetPassword(){
        //Instantiate the request queue
        queue = Volley.newRequestQueue(this);
        // make the PUT request
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response here
                        Toast.makeText(ResetPasswordActivity.this, "Update Successful!", Toast.LENGTH_LONG).show();
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
                params.put("email", user_email);
                params.put("password", password);
                return params;
            }
        };
        queue.add(putRequest);
    }

    public void backToProfile(View view) {
        startActivity(new Intent(ResetPasswordActivity.this, ProfileActivity.class));
    }
}