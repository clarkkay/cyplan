package com.example.frontend.helpers;

/**
 * This class contains methods frequently used on Strings
 * throughout the rest of the application.
 */
public class StringHelper {

    /**
     * Check that email is an @iastate email.
     *
     * @param email
     * @return
     */
    public static boolean validateEmail(String email){
        String regex = "([a-zA-Z0-9]+(?:[._+-][a-zA-Z0-9]+)*)@iastate.edu";
        return email.matches(regex);
    }
}
