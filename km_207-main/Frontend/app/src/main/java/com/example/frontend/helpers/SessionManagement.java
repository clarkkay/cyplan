package com.example.frontend.helpers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * This class was created for ease of access and control of a SharedPreferences object.
 * Admins can use this class to interact with a SharedPreferences object without knowing
 * how SharedPreferences works behind the scenes.
 */
public class SessionManagement {

    // SharedPreferences stores key value pairs.
    SharedPreferences sharedPreferences;

    // This can be thought of as a filename in the SharedPreferences "folder"
    String SHARED_PREF_NAME = "session";


    // Keys for user data
    String KEY_USER_NAME = "user_name";
    String KEY_USER_EMAIL = "user_email";
    String KEY_USER_TYPE = "user_type";
    String KEY_USER_ID = "user_id";
    String KEY_USER_PLAN = "user_plan";
    String KEY_USER_MAJOR = "user_major";

    String KEY_PLAN_OWNER_ID = "plan_owner_id";
    String KEY_PLAN_OWNER_NAME = "plan_owner_name";
    String KEY_PLAN_OWNER_EMAIL = "plan_owner_email";
    String KEY_PLAN_OWNER_MAJOR = "plan_owner_major";

    /**
     * Constructor accesses sharedPreferences information corresponding to "session" in sharedPreferences.
     * @param context
     */
    public SessionManagement(Context context) {
        // This tells sharedPreferences to look in the SHARED_PREF_NAME "file".
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * This saves the userName, email (unique ID), and userType (S or A) to SharedPreferences.
     * @param userName
     * @param userEmail
     * @param userType
     * @param userId
     */
    public void saveSession(String userName, String userEmail, String userType, int userId, String userMajor) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_EMAIL, userEmail);
        editor.putString(KEY_USER_TYPE, userType);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_MAJOR, userMajor);
        editor.apply();
    }

    /**
     * Returns the user name associated with the logged-in session.
     * @return user name
     */
    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    public void setUserName(String name) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    /**
     * Returns the user email associated with the logged-in session.
     * @return user email
     */
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    public void setUserEmail(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    /**
     * Returns the user type associated with the logged-in session.
     * @return user type
     */
    public String getUserType() {
        return sharedPreferences.getString(KEY_USER_TYPE, null);
    }

    /**
     * Returns the ID of the user associated with the logged-in session.
     * @return user ID
     */
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    /**
     * set the user ID (ONLY USED FOR TESTING PURPOSES)
     * @param id
     */
    public void setUserID(int id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_PLAN_OWNER_ID, id);
        editor.apply();
    }

    /**
     * Returns the major of a user associated with the logged-in session.
     * @return
     */
    public String getUserMajor() {
        return sharedPreferences.getString(KEY_USER_MAJOR, null);
    }

    public void setUserMajor(String major) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_MAJOR, major);
        editor.apply();
    }

    /**
     * Returns the last selected user plan
     * @return user plan
     */
    public String getUserPlan() {
        return sharedPreferences.getString(KEY_USER_PLAN, null);
    }

    /**
     * sets the user plan to the last one that they clicked on
     * @param plan
     */
    public void setUserPlan(String plan) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_PLAN, plan);
        editor.apply();
    }

    /**
     * Removes all the information saved in SharedPreferences,
     * effectively logging-out the user.
     */
    public void removeSession() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * set the plan owner id
     * @param owner_id
     */
    public void setPlanOwnerID(int owner_id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_PLAN_OWNER_ID, owner_id);
        editor.apply();
    }

    public void resetPlanOwnerID(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int user_id = getUserId();
        editor.putInt(KEY_PLAN_OWNER_ID, user_id);
        editor.apply();
    }

    public int getPlanOwnerID(){
        return sharedPreferences.getInt(KEY_PLAN_OWNER_ID, -1);
    }


    public void setPlanOwnerName(String owner_name){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PLAN_OWNER_NAME, owner_name);
        editor.apply();
    }

    public String getPlanOwnerName(){
        return sharedPreferences.getString(KEY_PLAN_OWNER_NAME, null);
    }

    public String getPlanOwnerEmail() {
        return sharedPreferences.getString(KEY_PLAN_OWNER_EMAIL, null);
    }

    public void setPlanOwnerEmail(String owner_email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PLAN_OWNER_EMAIL, owner_email);
        editor.apply();
    }

    public String getPlanOwnerMajor() {
        return sharedPreferences.getString(KEY_PLAN_OWNER_MAJOR, null);
    }

    public void setPlanOwnerMajor(String owner_major) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PLAN_OWNER_MAJOR, owner_major);
        editor.apply();
    }
}

