package com.example.frontend;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.frontend.helpers.GlobalVariableHelper;
import com.example.frontend.helpers.SpinnerHelper;
import com.example.frontend.helpers.StringHelper;

import java.sql.Array;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * This activity provides a GUI for users to create an account.
 * They enter their information and are then directed back to the
 * login screen.
 */
public class CreateAccountActivity extends AppCompatActivity {

    Button sign_up;

    TextView back_to_log_in;
    EditText first_name, email_address, make_password, verify_password;

    Spinner choose_major;

    CheckBox is_advisor;

    Boolean advisor = false;

    String email, firstName, password, verifyPassword, selected_major;
    String ip = GlobalVariableHelper.ip;

    // Server URL
    String url = ip + "/user/register";

    String user_type = "S";

    /**
     * Declares values for all of the things on the screen.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Hook edit text fields
        first_name = findViewById(R.id.firstName);
        email_address = findViewById(R.id.emailAddress);
        make_password = findViewById(R.id.makePassword);
        verify_password = findViewById(R.id.makePassword2);

        // Hook buttons
        sign_up = findViewById(R.id.signUpButton);
        back_to_log_in = findViewById(R.id.alreadyHaveAccount);

        // Hook spinners
        choose_major = findViewById(R.id.majorSpinner);

        // Hook checkbox
        is_advisor = findViewById(R.id.advisorCheckBox);

        // Create an ArrayAdapter using the helper
        ArrayAdapter<CharSequence> adapter = SpinnerHelper.createSimpleSpinner(this, R.array.majors_array);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        choose_major.setAdapter(adapter);
        selected_major = (String) choose_major.getSelectedItem();

        // click listener for the advisor check box
        is_advisor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    advisor = true;
                    user_type = "A";
                    choose_major.setVisibility(View.GONE); // Hide spinner if CheckBox is checked
                } else {
                    advisor = false;
                    user_type = "S";
                    choose_major.setVisibility(View.VISIBLE); // Show spinner if CheckBox is unchecked
                }
            }
        });
    }

    /**
     * Verifies that user has entered information before going back to log in
     * @param view
     */
    public void goToLogIn(View view) {
        selected_major = (String) choose_major.getSelectedItem();
        if (processFormFields(advisor)) {
            Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Takes the user back to the login screen.
     * @param view
     */
    public void alreadyHaveAccount(View view) {
        Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * process form fields and determine if user is advisor
     * @param advisor
     * @return
     */
    public boolean processFormFields(Boolean advisor) {
        // they are an advisor
        if (advisor && validateFirstName() && validateEmail() && validatePassword() && validateConfirmPassword() && passwordMatch()) {
            // POST the account
            selected_major = "COM S";
            postAccount();
            Toast.makeText(CreateAccountActivity.this, "Registration successful!", Toast.LENGTH_LONG).show();
            return true;
        } else if (validateFirstName() && validateEmail() && validatePassword() && validateConfirmPassword() && passwordMatch() && validateMajor()) {
            // POST the account
            postAccount();
            Toast.makeText(CreateAccountActivity.this, "Registration successful!", Toast.LENGTH_LONG).show();
            return true;
        } else {
            return false;
        }
    }


    /**
     * Checks whether a name has been entered.
     * @return name validity
     */
    public boolean validateFirstName() {
        firstName = first_name.getText().toString();

        // checks whether name is empty
        if (firstName.isEmpty()) {
            first_name.setError("Please enter your first name");
            return false;
        } else {
            first_name.setError(null);
            return true;
        }
    }

    /**
     * Checks whether a user has selected a major.
     * @return major validity
     */
    public boolean validateMajor(){
        if (selected_major.equals("Select Major")){
            Toast.makeText(CreateAccountActivity.this, "Please choose a major", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    /**
     * Checks whether the email field is empty and ensures it is
     * an @iastate.edu address.
     * @return email validity
     */
    public boolean validateEmail() {
        email = email_address.getText().toString();

        // checks whether email is empty
        if (email.isEmpty()) {
            email_address.setError("Please enter your email");
            return false;
        } else if (!StringHelper.validateEmail(email)) {
            email_address.setError("Please enter your @iastate email");
            return false;
        }
        else {
            email_address.setError(null);
            return true;
        }
    }

    /**
     * Checks whether password field is empty.
     * @return password validity.
     */
    public boolean validatePassword() {
        password = make_password.getText().toString();

        if (password.isEmpty()) {
            make_password.setError("Please enter a valid password");
            return false;
        } else {
            make_password.setError(null);
            return true;
        }
    }

    /**
     * Checks whether password confirmation field is empty.
     * @return password confirmation validity
     */
    public boolean validateConfirmPassword() {
        verifyPassword = verify_password.getText().toString();

        if (verifyPassword.isEmpty()) {
            verify_password.setError("This field cannot be empty");
            return false;
        // check that the first password is filled in because the user should have a password to validate it
        } else if (password.isEmpty()){
            make_password.setError("This field cannot be empty");
            return false;
        }else {
            verify_password.setError(null);
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
            verify_password.setError("Passwords must match");
            return false;
        } else {
            verify_password.setError(null);
            return true;
        }
    }

    /**
     * Sends the user information to the back end via a POST method.
     * Creates the user account in the DB.
     */
    public void postAccount () {
        //Instantiate the request queue
        RequestQueue queue = Volley.newRequestQueue(CreateAccountActivity.this);

        //String request object
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equalsIgnoreCase(" Successful")) {
                    first_name.setText(null);
                    email_address.setText(null);
                    make_password.setText(null);
                    verify_password.setText(null);
                    // reset to default
                    user_type = "S";
                    // set the selected major back to the hint
                    choose_major.setSelection(0);
                    selected_major = "";

                    Toast.makeText(CreateAccountActivity.this, "Registration successful!", Toast.LENGTH_LONG).show();
                }
                //End of response if block

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CreateAccountActivity.this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                Log.e("VolleyError", "Error: " + error.toString());
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //post the information to SB
                params.put("first_name", firstName);
                params.put("email", email);
                params.put("password", password);
                params.put("user_type", user_type);
                params.put("major", selected_major);
                return params;
            }

        };//End of String Req Object
        queue.add(stringRequest);
    }


}