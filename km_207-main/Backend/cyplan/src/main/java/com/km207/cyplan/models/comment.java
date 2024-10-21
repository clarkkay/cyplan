package com.km207.cyplan.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="comments")
public class comment {
    @Id
    @Column(name="user_comment")
    private String user_comment;

    @Column(name="course_code")
    private String course_code;

    public String getUser_comment(){
        return user_comment;
    }

    public void setUser_comment(String newComment){
        this.user_comment = newComment;
    }

    public String getCourse_code(){
        return this.course_code;
    }

    public void setCourse_code(String newCourseCode){
        this.course_code = newCourseCode;
    }
}
