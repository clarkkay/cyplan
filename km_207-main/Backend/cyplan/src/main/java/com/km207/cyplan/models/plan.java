package com.km207.cyplan.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
    /*
    StudentPlans Table (to represent students' class registration plans):
    PlanID (Primary Key)
    StudentID (Foreign Key referencing Students table)
    ClassID (Foreign Key referencing Classes table)
    Semester
    Year
     */
@Entity
@Table(name = "plans")
public class plan {
    public int getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(int plan_id) {
        this.plan_id = plan_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getCourse_code() {
        return course_code;
    }

    public void setCourse_code(int course_code) {
        this.course_code = course_code;
    }

    public int getSemester_taken() {
        return semester_taken;
    }

    public void setSemester_taken(int semester_taken) {
        this.semester_taken = semester_taken;
    }

    public int getPlan_name() {
        return plan_name;
    }

    public void setPlan_name(int plan_name) {
        this.plan_name = plan_name;
    }

    @Id
    @Column(name = "plan_id")
    private int plan_id;

    @Column(name = "user_id")
    private int user_id;

    @Column(name = "course_code")
    private int course_code;
    @Column(name = "semester_taken")
    private int semester_taken;
    @Column(name = "plan_name")
    private int plan_name;

}
