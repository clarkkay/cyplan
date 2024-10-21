package com.km207.cyplan.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "friends")
public class friends {
    public int getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(int friend_id) {
        this.friend_id = friend_id;
    }

    public String getcurrentUseremail() {
        return currentUseremail;
    }

    public void setcurrentUseremail(String currentUserEmail) {
        this.currentUseremail = currentUserEmail;
    }

    public String getFriendEmail() {
        return friendEmail;
    }

    public void setFriendEmail(String friendEmail) {
        this.friendEmail = friendEmail;
    }

    @Id
    @Column(name = "friend_id")
    private int friend_id;

    @Column(name = "currentUseremail")
    private String currentUseremail;
    @Column(name = "friendEmail")
    private String friendEmail;

    public String getPendingFriendEmail() {
        return pendingFriendEmail;
    }

    public void setPendingFriendEmail(String pendingFriendEmail) {
        this.pendingFriendEmail = pendingFriendEmail;
    }

    @Column(name = "pendingFriendEmail")
    private String pendingFriendEmail;
    @Column(name = "status")
    private String status;
}
