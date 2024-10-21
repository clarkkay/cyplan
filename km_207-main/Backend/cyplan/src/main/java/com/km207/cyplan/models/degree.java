package com.km207.cyplan.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="degrees")
public class degree {
    @Id
    @Column(name = "major")
    private String major;

    @Column(name = "degree_name")
    private String degree_name;

    @Column(name = "college")
    private String college;

    // Getter and Setter methods for major
    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    // Getter and Setter methods for degreeName
    public String getDegreeName() {
        return degree_name;
    }

    public void setDegreeName(String degreeName) {
        this.degree_name = degreeName;
    }

    // Getter and Setter methods for college
    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }
}
