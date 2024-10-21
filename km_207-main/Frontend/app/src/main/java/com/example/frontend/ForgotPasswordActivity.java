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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.frontend.helpers.GlobalVariableHelper;
import com.example.frontend.helpers.SessionManagement;
import com.example.frontend.helpers.StringHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText editEmailAddress;
    String email;

    String ip = GlobalVariableHelper.ip;
    String url = ip + "/user/forgotPassword";

    Button passwordResetButton, backToLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editEmailAddress = findViewById(R.id.editEmailAddress);
        passwordResetButton = findViewById(R.id.passwordResetButton);
        backToLoginButton = findViewById(R.id.backToLoginButton);
    }

    /**
     * passwordResetButton is mapped to this via onClick.
     * @param view
     */
    public void sendPasswordReset(View view) {
        email = editEmailAddress.getText().toString();

//        if (email.isEmpty()){
//            editEmailAddress.setError("Please enter your Iowa State email to send your password");
//            // check that it's a valid @iastate email
//        } else if (!StringHelper.validateEmail(email)){
//            editEmailAddress.setError("Please enter your @iastate email to send your password");
//        } else {
//            editEmailAddress.setError(null);
            forgotPasswordRequest();
//        }

    }

    /**
     * backToLoginButton is mapped to this via onClick.
     * @param view
     */
    public void backToLogin(View view) {
        startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
    }

    /**
     *
     */
    public void forgotPasswordRequest() {

        RequestQueue queue = Volley.newRequestQueue(ForgotPasswordActivity.this);

        // String request object
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(ForgotPasswordActivity.this, response, Toast.LENGTH_LONG).show();
                startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ForgotPasswordActivity.this, "VolleyError", Toast.LENGTH_LONG).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Create a map of parameters to send with the POST request.
                Map<String, String> params = new HashMap<>();
                // Post the information to the backend.
                params.put("email", email);
                return params;
            }

        };//End of String Req Object
        queue.add(stringRequest);
    }

}