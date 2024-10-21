package com.example.frontend;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.*;

import android.provider.ContactsContract;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FrontTests {

    private static final int SIMULATED_DELAY_MS = 1000;

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        init();
    }

    @After
    public void tearDown() {
        release();
    }

    @Test
    public void registerStudentLoginStudent() throws InterruptedException {
        Random rand = new Random();
        float randomFloat = rand.nextFloat();
        // Navigate to the registration screen
        onView(withId(R.id.signUpButton)).perform(click());

        // Fill out the registration form
        onView(withId(R.id.firstName)).perform(typeText("John" + String.valueOf(randomFloat)), closeSoftKeyboard());
        onView(withId(R.id.emailAddress)).perform(typeText("john" + String.valueOf(randomFloat) + "@iastate.edu"), closeSoftKeyboard());
        onView(withId(R.id.makePassword)).perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.makePassword2)).perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.majorSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("COM S"))).perform(click());

        // Submit the registration form
        onView(withId(R.id.signUpButton)).perform(scrollTo(), click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Verify that the MainActivity is opened after registration
        intended(hasComponent(MainActivity.class.getName()));

        onView(withId(R.id.editEmailAddress)).perform(typeText("john1@iastate.edu"), closeSoftKeyboard());
        onView(withId(R.id.editPassword)).perform(typeText("password123"), closeSoftKeyboard());

        // click log in button
        onView(withId(R.id.logInButton)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Verify that the HomeActivity is opened after registration
        intended(hasComponent(HomeActivity.class.getName()));

        // log out
        // hit the menu button
        onView(withId(R.id.menuButton)).perform(click());
        // hit logout
        onView(withId(R.id.logOutButton)).perform(click());
    }

    private void loginStudent(){
        onView(withId(R.id.editEmailAddress)).perform(typeText("john1@iastate.edu"), closeSoftKeyboard());
        onView(withId(R.id.editPassword)).perform(typeText("password123"), closeSoftKeyboard());

        // click log in button
        onView(withId(R.id.logInButton)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}
    }

    @Test
    public void registerAdvisor() throws InterruptedException {
        // Navigate to the registration screen
        onView(withId(R.id.signUpButton)).perform(click());

        // Fill out the registration form
        onView(withId(R.id.firstName)).perform(typeText("Advisor1"), closeSoftKeyboard());
        onView(withId(R.id.emailAddress)).perform(typeText("advisor1@iastate.edu"), closeSoftKeyboard());
        onView(withId(R.id.makePassword)).perform(typeText("123"), closeSoftKeyboard());
        onView(withId(R.id.makePassword2)).perform(typeText("123"), closeSoftKeyboard());
        onView(withId(R.id.majorSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("COM S"))).perform(click());
        onView(withId(R.id.advisorCheckBox)).perform(click());

        // Submit the registration form
        onView(withId(R.id.signUpButton)).perform(scrollTo(), click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}
        // Verify that the MainActivity is opened after registration
        intended(hasComponent(MainActivity.class.getName()));

        onView(withId(R.id.editEmailAddress)).perform(typeText("advisor1@iastate.edu"), closeSoftKeyboard());
        onView(withId(R.id.editPassword)).perform(typeText("123"), closeSoftKeyboard());

        // click log in button
        onView(withId(R.id.logInButton)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Verify that the HomeActivity is opened after registration
        intended(hasComponent(DropDownMenu.class.getName()));

        // hit logout
        onView(withId(R.id.logOutButton)).perform(click());
    }

    private void loginAdvisor(){
        onView(withId(R.id.editEmailAddress)).perform(typeText("advisor1@iastate.edu"), closeSoftKeyboard());
        onView(withId(R.id.editPassword)).perform(typeText("123"), closeSoftKeyboard());

        // click log in button
        onView(withId(R.id.logInButton)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Verify that the HomeActivity is opened after registration
        intended(hasComponent(DropDownMenu.class.getName()));
    }

    @Test
    public void addSemester(){
        loginStudent();

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // scroll to the add semester and add semester
        onView(withId(R.id.addSemesterButton)).perform(scrollTo()).perform(click());

        // logout
        // hit the menu button
        onView(withId(R.id.menuButton)).perform(scrollTo()).perform(click());
        // hit logout
        onView(withId(R.id.logOutButton)).perform(click());
        // Verify that the HomeActivity is opened after registration
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void rateCourse(){
        String courseDescription = "MATH 165 Semester 1";

        loginStudent();

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // click on a course for the popup
        onView(withContentDescription(courseDescription)).perform(click());

        // click on a rating
        onView(withId(R.id.ratingBar)).perform(click());

        // rate it
        onView(withText("Rate")).perform(click());

        // logout
        // hit the menu button
        onView(withId(R.id.menuButton)).perform(click());
        // hit logout
        onView(withId(R.id.logOutButton)).perform(click());
        // Verify that the HomeActivity is opened after registration
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void chooseCourse() throws InterruptedException {
        Random rand = new Random();
        int randomInt = rand.nextInt();
        String courseDescription = "MATH 165 Semester 1";
        String expectedDescription = "Basic Calculus provides an introduction to differential and integral calculus. Applications of differentiation, including optimization, are explored.";

        loginStudent();

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // click on a course for the popup
        onView(withContentDescription(courseDescription)).perform(click());

        // verify the description
        onView(withId(R.id.course_details_text))
                .check(matches(withText(expectedDescription)));


        // comment on the course
        String comment = "Test Comment" + String.valueOf(randomInt);
        onView(withId(R.id.comment_input)).perform(typeText(comment), closeSoftKeyboard());
        onView(withText("Comment")).perform(click());

        onView(anyOf(withText(comment))).check(matches(isDisplayed()));

        // log out
        // hit the exit button
        onView(withText("Exit")).perform(click());
        // hit the menu button
        onView(withId(R.id.menuButton)).perform(click());
        // hit logout
        onView(withId(R.id.logOutButton)).perform(click());
    }

    @Test
    public void TestInvalidPrereq(){
        loginStudent();
        // manually set the view with the prereqs wrong
        ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class);
        scenario.onActivity(activity -> {
            List<String> semester1 = Arrays.asList("ENGL 150");
            List<String> semester2 = Arrays.asList("LIB 160");

            activity.semesterList.clear(); // Clear existing data
            activity.semesterList.add(semester1);
            activity.semesterList.add(semester2);

            activity.checkPreReqs();
        });

        // hit the menu button
        onView(withId(R.id.menuButton)).perform(click());
        // hit logout
        onView(withId(R.id.logOutButton)).perform(click());
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void AddCourse(){
        String courseDescription = "COM S 127 Semester 0";
        loginStudent();
        // manually call the search for course
        ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class);
        scenario.onActivity(activity -> {
            // add COM S 127 to Semester 0
            activity.searchForCourse("COM S 127");
        });

        // hit the menu button
        onView(withId(R.id.menuButton)).perform(click());
        // hit logout
        onView(withId(R.id.logOutButton)).perform(click());
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void goToSavedPlans(){
        loginStudent();
        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        intended(hasComponent(HomeActivity.class.getName()));

        // click on the menu button
        onView(withId(R.id.menuButton)).perform(click());

        // click on the got to saved plans button
        onView(withId(R.id.viewSavedPlansButton)).perform(click());

        // assert that we are at the saved plans
        intended(hasComponent(SavedPlansActivity.class.getName()));

        // logout
        // hit the menu button
        onView(withId(R.id.menuButton)).perform(click());
        // hit logout
        onView(withId(R.id.logOutButton)).perform(click());
        // Verify that the HomeActivity is opened after registration
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void goToFriendsAddFriend(){
        loginStudent();
        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        intended(hasComponent(HomeActivity.class.getName()));

        // hit the menu button
        onView(withId(R.id.menuButton)).perform(click());

        // go to your friends
        onView(withId(R.id.chatsButton)).perform(click());

        intended(hasComponent(AllChatsActivity.class.getName()));

        // search for a friend
        onView(withId(R.id.search_user_btn)).perform(click());
        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}
        onView(withId(R.id.search_input)).perform(typeText("prolling@iastate.edu"), closeSoftKeyboard());
        onView(withId(R.id.search_user_btn)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // click on them
        onView(withText("Paige"))
                .perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // send a friend request
        onView(withId(R.id.addFriendTextView)).perform(click());

        // click on their plans button (it won't work)
        onView(withId(R.id.plansButton)).perform(click());

        // logout
        // hit the menu button
        onView(withId(R.id.menuButton)).perform(click());
        // hit logout
        onView(withId(R.id.logOutButton)).perform(click());

        // log into Paige's account
        onView(withId(R.id.editEmailAddress)).perform(typeText("prolling@iastate.edu"), closeSoftKeyboard());
        onView(withId(R.id.editPassword)).perform(typeText("123"), closeSoftKeyboard());

        // click log in button
        onView(withId(R.id.logInButton)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // hit the menu button
        onView(withId(R.id.menuButton)).perform(click());

        // go to your friends
        onView(withId(R.id.chatsButton)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // click on accept for the friend request
        onView(withText("Accept"))
                .perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // click on friend
        onView(withText("john1@iastate.edu"))
                .perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // click on their plans (it will work now)
        onView(withId(R.id.plansButton)).perform(click());

        // remove friend in order to make it so we can run this test again successfully
        onView(withId(R.id.menuButton)).perform(click());
        onView(withId(R.id.chatsButton)).perform(click());
        onView(withText("john1@iastate.edu"))
                .perform(click());
        // remove friend
        onView(withId(R.id.addFriendTextView)).perform(click());

        // logout
        // hit the menu button
        onView(withId(R.id.menuButton)).perform(click());
        // hit logout
        onView(withId(R.id.logOutButton)).perform(click());
    }

    @Test
    public void goToForgotPassword(){
        onView(withId(R.id.forgotPassword)).perform(click());
        intended(hasComponent(ForgotPasswordActivity.class.getName()));

        onView(withId(R.id.backToLoginButton)).perform(click());
        onView(withId(R.id.forgotPassword)).perform(click());

        onView(withId(R.id.editEmailAddress)).perform(typeText("test@iastate.edu"), closeSoftKeyboard());
        onView(withId(R.id.passwordResetButton)).perform(click());
    }

    @Test
    public void updatePassword(){
        loginStudent();

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        intended(hasComponent(HomeActivity.class.getName()));

        // click on menu button
        onView(withId(R.id.menuButton)).perform(click());

        // go to profile
        onView(withId(R.id.profileButton)).perform(click());

        // click on the update password button
        onView(withId(R.id.editProfileButton)).perform(click());
        onView(withId(R.id.changePasswordButton)).perform(click());
        intended(hasComponent(ResetPasswordActivity.class.getName()));
        onView(withId(R.id.backToProfileButton)).perform(click());
        onView(withId(R.id.editProfileButton)).perform(click());
        onView(withId(R.id.changePasswordButton)).perform(click());

        // update the password
        onView(withId(R.id.makePassword)).perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.makePassword2)).perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.passwordResetButton)).perform(click());
        onView(withId(R.id.backToProfileButton)).perform(click());

        // logout
        // hit the menu button
        onView(withId(R.id.menuButton)).perform(click());
        // hit logout
        onView(withId(R.id.logOutButton)).perform(click());
        intended(hasComponent(MainActivity.class.getName()));
    }

    /////////////////////////////////////////////////////////////
    @Test
    public void goToProfile(){
        loginStudent();

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}
        intended(hasComponent(HomeActivity.class.getName()));

        // click on the menu button
        onView(withId(R.id.menuButton)).perform(click());
        // click on the profile button
        onView(withId(R.id.profileButton)).perform(click());
        // check that the profile loads stuff
        intended(hasComponent(ProfileActivity.class.getName()));
        // hit the menu button
        onView(withId(R.id.menuButton)).perform(click());
        // hit logout
        onView(withId(R.id.logOutButton)).perform(click());
        // Verify that the HomeActivity is opened after registration
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void changeProfile(){
        loginStudent();

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        intended(hasComponent(HomeActivity.class.getName()));

        // go to the profile
        // click on the menu button
        onView(withId(R.id.menuButton)).perform(click());
        // click on the profile button
        onView(withId(R.id.profileButton)).perform(click());
        // check that the profile loads stuff
        intended(hasComponent(ProfileActivity.class.getName()));

        // click on the gear
        onView(withId(R.id.editProfileButton)).perform(click());

        // change your information
        onView(withId(R.id.nameEditText)).perform(replaceText("new name"), closeSoftKeyboard());

        // click the gear again
        onView(withId(R.id.editProfileButton)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // check that the new information you put in is displayed on the screen
        onView(withId(R.id.nameEditText)).check(matches(withText("new name")));

        // logout
        // hit the menu button
        onView(withId(R.id.menuButton)).perform(click());
        // hit logout
        onView(withId(R.id.logOutButton)).perform(click());
        // Verify that the HomeActivity is opened after registration
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void goToFriends(){
        loginStudent();
        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        intended(hasComponent(HomeActivity.class.getName()));

        // click on the menu
        onView(withId(R.id.menuButton)).perform(click());

        // click on the friends button
        onView(withId(R.id.chatsButton)).perform(click());

        // assert that we are on the friends page
        intended(hasComponent(AllChatsActivity.class.getName()));

        // logout
        // hit the menu button
        onView(withId(R.id.menuButton)).perform(click());
        // hit logout
        onView(withId(R.id.logOutButton)).perform(click());
        // Verify that the HomeActivity is opened after registration
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void advisorSearchStudent(){
        loginAdvisor();

        // click on the search student
        onView(withId(R.id.searchStudentButton)).perform(click());

        // search a student (search for prolling@iastate.edu)
        onView(withId(R.id.search_input)).perform(typeText("prolling@iastate.edu"), closeSoftKeyboard());
        onView(withId(R.id.search_user_btn)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // click on their profile
        onView(withText("Paige"))
                .perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // go to their plans
        onView(withId(R.id.plansButton)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // click on "Paige's Plan 1"
        onView(withText("Paige's Plan 1"))
                .perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        intended(hasComponent(HomeActivity.class.getName()));

        // logout
        // hit the menu button
        onView(withId(R.id.menuButton)).perform(click());
        // hit logout
        onView(withId(R.id.logOutButton)).perform(click());
        intended(hasComponent(MainActivity.class.getName()));
    }

}
