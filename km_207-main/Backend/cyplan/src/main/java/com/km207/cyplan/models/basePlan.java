package com.km207.cyplan.models;

import java.util.List;

/*
 *This is an object to store a majors base plan. A base plan is comprised of specific courses that have
 *to be taken certain semesters, as well as "clusters" which are groupings of courses that are a
 *selection of courses that the user needs to choose one to take during a given semester.
 */
public class basePlan {
    private String major;
    private List<String> classes;
    private List<cluster> clusters;

    //getter & setter for major
    public String getMajor(){ return this.major; }
    public void setMajor(String newMajor){ this.major = newMajor; }

    //getter & setter for classes
    public List<String> getClasses(){ return this.classes; }
    public void setClasses(List<String> newClasses){this.classes = newClasses; }

    //getter & setter for optionals
    public List<cluster> getClusters(){ return this.clusters; }
    public void setClusters(List<cluster> newClusters){ this.clusters = newClusters; }
}
