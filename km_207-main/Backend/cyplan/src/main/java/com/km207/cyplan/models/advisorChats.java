package com.km207.cyplan.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "advisorChats")
public class advisorChats {
    @Id
    @Column(name = "chat_id")
    private int chat_id;

    @Column(name = "advisorEmail")
    private String advisorEmail;
    @Column(name = "adviseeEmail")
    private String adviseeEmail;

    public String getPlan_name() {
        return plan_name;
    }

    public void setPlan_name(String plan_name) {
        this.plan_name = plan_name;
    }

    @Column(name = "plan_name")
    private String plan_name;

    public int getChat_id() {
        return chat_id;
    }

    public void setChat_id(int chat_id) {
        this.chat_id = chat_id;
    }

    public String getAdvisorEmail() {
        return advisorEmail;
    }

    public void setAdvisorEmail(String advisorEmail) {
        this.advisorEmail = advisorEmail;
    }

    public String getAdviseeEmail() {
        return adviseeEmail;
    }

    public void setAdviseeEmail(String adviseeEmail) {
        this.adviseeEmail = adviseeEmail;
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    @Column(name = "chat")
    private String chat;
}
