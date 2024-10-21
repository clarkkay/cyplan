package com.km207.cyplan.models;

import jakarta.persistence.*;


@Entity
@Table(name = "users")
public class user {
    @Id
    @Column(name = "user_id")
    private int user_id;
    @Column(name = "first_name")
    private String first_name;
    @Column(name = "email")
    private String email;
    @Column(name = "major")
    private String major;
    @Column(name = "password")
    private String password;
    @Column(name = "user_type")
    private String user_type;



    //Constructor
    public user(String first_name, String email, String major, String user_type, String password){
        this.first_name = first_name;
        this.email = email;
        this.major = major;
        this.user_type = user_type;
        this.password = password;
    }

    public user() {

    }

    //GETTERS AND SETTERS

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }


    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }


    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
