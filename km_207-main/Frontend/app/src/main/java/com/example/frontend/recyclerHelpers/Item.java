package com.example.frontend.recyclerHelpers;

/**
 * This class provides a blueprint for items to be displayed in a RecyclerView.
 * The Items are users.
 */
public class Item {

//    String name;
    String email;
//    String major;

    /**
     * Constructor for an Item.
     * @param email
     */
    public Item(String email) {
//        this.name = name;
        this.email = email;
//        this.major = major;
    }

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    /**
     * Gets the email of an Item.
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of an Item.
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

//    public String getMajor() {
//        return major;
//    }
//
//    public void setMajor(String major) {
//        this.major = major;
//    }
}
