package com.example.frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.frontend.helpers.SessionManagement;

/**
 * This activity provides a dropdown menu containing buttons to many
 * other activities and features throughout the app.
 */
public class DropDownMenu extends AppCompatActivity {

    Button home, profile, viewPlans, logOut, searchStudent, chats;

    SessionManagement sessionManagement;

    int user_id;

    String user_type;

    /**
     * Declares values for all of the things on the screen.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drop_down_menu);

        // student buttons
        home = findViewById(R.id.homeButton);
        profile = findViewById(R.id.profileButton);
        viewPlans = findViewById(R.id.viewSavedPlansButton);
        chats = findViewById(R.id.chatsButton);

        // both buttons
        logOut = findViewById(R.id.logOutButton);

        // advisor buttons
        searchStudent = findViewById(R.id.searchStudentButton);

        // get information
        sessionManagement = new SessionManagement(DropDownMenu.this);
        user_id = sessionManagement.getUserId();
        user_type = sessionManagement.getUserType();
        // whenever the drop down menu is clicked, we set the plan owner back for safety
        sessionManagement.setPlanOwnerID(user_id);

        sessionManagement.resetPlanOwnerID();

        // set the drop down to either student or advisor
        if (user_type.equals("student") || user_type.equals("S")){
            searchStudent.setVisibility(View.GONE);
        } else {
            home.setVisibility(View.GONE);
            profile.setVisibility(View.GONE);
            viewPlans.setVisibility(View.GONE);
            chats.setVisibility(View.GONE);
        }
    }

    /**
     * Test button to test the limited access of the schedule builder
     * DELETE AFTER TESTING IS COMPLETE
     * @param view
     */
    public void testSafeAccess(View view){
        sessionManagement.setPlanOwnerID(user_id);
        sessionManagement.setUserID(5);
        Intent intent = new Intent(DropDownMenu.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Takes user to their saved plans
     * @param view
     */
    public void goToSavedPlans(View view){
        sessionManagement.setPlanOwnerID(user_id);
        Intent intent = new Intent(DropDownMenu.this, SavedPlansActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Takes users to the home screen.
     * @param view
     */
    public void goToHome(View view) {
        Intent intent = new Intent(DropDownMenu.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Takes users to the profile screen.
     * @param view
     */
    public void goToProfile(View view) {
        Intent intent = new Intent(DropDownMenu.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Takes users to the chats screen.
     * @param view
     */
    public void goToViewChats(View view) {
        startActivity(new Intent(DropDownMenu.this, AllChatsActivity.class));

    }

    /**
     * Logs-out the user. Takes them back to the login screen.
     * @param view
     */
    public void goToLogOut(View view) {
        SessionManagement sessionManagement = new SessionManagement(DropDownMenu.this);
        sessionManagement.removeSession();
        // implement clearing the cache
        Intent intent = new Intent(DropDownMenu.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToSearchStudent(View view){
        Intent intent = new Intent(DropDownMenu.this, FriendsActivity.class);
        startActivity(intent);
        finish();
    }
}