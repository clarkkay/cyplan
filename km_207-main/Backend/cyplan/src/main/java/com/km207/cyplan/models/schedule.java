package com.km207.cyplan.models;

import java.util.ArrayList;
import java.util.List;

public class schedule {
    List<String> classes;
    int userID;
    String planName;

    //getter and setter for classes
    public void setClasses(List<String> newClasses){ this.classes = newClasses; }
    public List<String> getClasses(){ return this.classes; }

    //getter and setter for user_id
    public void setUserID(int userID) { this.userID = userID; }
    public int getUserID(){ return this.userID; }

    //getter and setter for plan name

    public void setPlanName(String planName) { this.planName = planName; }
    public String getPlanName(){ return this.planName; }
}
