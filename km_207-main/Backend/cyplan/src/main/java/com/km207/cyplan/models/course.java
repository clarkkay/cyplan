package com.km207.cyplan.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "courses")
public class course {
    @Id
    @Column(name = "course_code")
    private String courseCode;

    @Column(name = "course_name")
    private String courseName;

    @Column(name = "preReqs")
    private String preReqs;

    @Column(name = "coReqs")
    private String coReqs;

    @Column(name = "credits")
    private int credits;

    @Column(name = "semesters_offered")
    private String semestersOffered;

    @Column(name = "total_rating")
    private float totalRating;

    @Column(name = "num_ratings")
    private int numRatings;

    // Getter and Setter methods for courseCode
    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    // Getter and Setter methods for courseName
    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    // Getter and Setter methods for preReqs
    public String getPreReqs() {
        return preReqs;
    }

    public void setPreReqs(String preReqs) {
        this.preReqs = preReqs;
    }

    // Getter and Setter methods for coReqs
    public String getCoReqs() {
        return coReqs;
    }

    public void setCoReqs(String coReqs) {
        this.coReqs = coReqs;
    }

    // Getter and Setter methods for credits
    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    // Getter and Setter methods for semestersOffered
    public String getSemestersOffered() {
        return semestersOffered;
    }

    public void setSemestersOffered(String semestersOffered) {
        this.semestersOffered = semestersOffered;
    }

    //Getter and Setter for rating stuff
    public float getTotalRating(){return totalRating;}

    public void setTotalRating(float newTotalRating){ this.totalRating = newTotalRating;}

    public int getNumRatings(){return numRatings;}

    public void setNumRatings(int newNumRatings){this.numRatings = newNumRatings;}

}
