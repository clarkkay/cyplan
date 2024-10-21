package com.km207.cyplan.models;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/*
 * A cluster object holds all neccesary information about a course cluster in a base plan for
 * the front end to use to display to users the couse options they have.
 */
public class cluster {
    private String title;
    private List<String> classes;
    private int semester;

    public cluster(){
        //default constructor
    }
    public cluster(String newTitle, List<String> newClasses, int newSemester){
        this.title = newTitle;
        this.classes = newClasses;
        this.semester = newSemester;
    }

    //Getter and Setter for title
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    //Getter and setter for classes
    public List<String> getClasses() { return classes; }
    public void setClasses(List<String> classes) { this.classes = classes; }

    //Getter and Setter for semester
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        final cluster other = (cluster) obj;
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        //Ordering of classes doesn't matter for clusters to be equal. As long as they have the same classes they are the same
        List<String> thisClassesSorted = this.classes;
        List<String> otherClassesSorted = other.classes;
        Collections.sort(thisClassesSorted);
        Collections.sort(otherClassesSorted);
        if (!Objects.equals(thisClassesSorted, otherClassesSorted)) {
            return false;
        }
        if(!Objects.equals(this.semester, other.semester)){
            return false;
        }
        return true;
    }
}
