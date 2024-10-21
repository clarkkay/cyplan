package com.km207.cyplan.websockets;

import com.km207.cyplan.repository.CoursesRepository;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
@Controller
@ServerEndpoint("/courseRating/{courseCode}")
//@Component
public class CourseRatingWebsocket {


    private static CoursesRepository coursesRepository;
    // Store all socket session and their corresponding username
    // Two maps for the ease of retrieval by key
    @Autowired
    public void setCoursesRepository(CoursesRepository crsRepo){
        coursesRepository = crsRepo;
    }
    private static Map<Session, String > sessionCourseCodeMap = new Hashtable< >();
    private static Map < String, Session > courseCodeSessionMap = new Hashtable < > ();
    // server side logger
    private final Logger logger = LoggerFactory.getLogger(CourseRatingWebsocket.class);

    /**
     * This method is called when a new WebSocket connection is established.
     *
     * @param session represents the WebSocket session for the connected user.
     * @param courseCode username specified in path parameter.
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("courseCode") String courseCode) throws IOException {

        // server side log
        logger.info("[onOpen] " + courseCode);

        // map current session with courseCode
        sessionCourseCodeMap.put(session, courseCode);

        // map current courseCode with session
        courseCodeSessionMap.put(courseCode, session);
    }

    /**
     * Handles incoming WebSocket messages from a client.
     *
     * @param session The WebSocket session representing the client's connection.
     * @param userRating The rating the user gave to the course
     */
    @OnMessage //TODO: call this "onRating"
    public void onMessage(Session session, String userRating) throws IOException {

        // get the username by session
        String courseCode = sessionCourseCodeMap.get(session);
        float userRatingFloat;
        try{
            userRatingFloat = Float.parseFloat(userRating);
        }
        catch(Exception e){  //TODO: make this catch a more specific error (not just all exceptions, but a parseing one) Maybe return something different too
            logger.info("ERROR parsing user rating of " + userRating + " into a float");
            return;
        }

        //get old Rating Database Values
        int oldNumRatings = coursesRepository.getNumRatings(courseCode);
        float oldTotalRating = coursesRepository.getTotalRating(courseCode);

        //calculate new rating database values
        int newNumRatings = oldNumRatings+1; //you are only adding 1 rating so it goes up by 1
        float newTotalRating = (oldTotalRating + userRatingFloat) / (float)newNumRatings; //this should calculate the new total rating

        int result = coursesRepository.updateCourseRating(courseCode, newTotalRating, newNumRatings); //update the database with the new values
        // server side log
        logger.info("[onMessage] " + courseCode + ": " + userRating);
        broadcast("{\"rating\": \"" + newTotalRating + "\"}");  //TODO: this may have to be changed
    }

    /**
     * Handles the closure of a WebSocket connection.
     *
     * @param session The WebSocket session that is being closed.
     */
    @OnClose
    public void onClose(Session session) throws IOException {

        // get the username from session-username mapping
        String courseCode = sessionCourseCodeMap.get(session);

        // server side log
        logger.info("[onClose] " + courseCode);

        // remove user from memory mappings
        sessionCourseCodeMap.remove(session);

        if (courseCode != null) {
            courseCodeSessionMap.remove(courseCode);
        } else {
            logger.warn("Course code is null for session: " + session.getId());
        }
    }


    /**
     * Handles WebSocket errors that occur during the connection.
     *
     * @param session   The WebSocket session where the error occurred.
     * @param throwable The Throwable representing the error condition.
     */
    @OnError
    public void onError(Session session, Throwable throwable) {

        // get the username from session-username mapping
        String username = sessionCourseCodeMap.get(session);

        // do error handling here
        logger.info("[onError]" + username + ": " + throwable.getMessage());
    }

    /**
     * Broadcasts a message to all users in the chat.
     *
     * @param rating The rating to be shown to all users.
     */
    private void broadcast(String rating) {
        sessionCourseCodeMap.forEach((session, courseCode) -> {
            try {
                session.getBasicRemote().sendText(rating); //this is the line that sends information to the front end
            } catch (IOException e) {
                logger.info("[Broadcast Exception] " + e.getMessage());
            }
        });
    }
    public boolean isCoursesRepositoryNull() {
        return coursesRepository == null;
    }

    // Method to check if the repository is empty
    public boolean isCoursesRepositoryEmpty() {
        // Assuming there is a method to count entries.
        // The actual method name may vary based on your repository implementation.
        return coursesRepository.count() == 0;
    }

    // Method to do both checks and log the status
    public void checkCoursesRepository() {
        if (isCoursesRepositoryNull()) {
            logger.error("coursesRepository is null!");
        } else if (isCoursesRepositoryEmpty()) {
            logger.info("coursesRepository is empty!");
        } else {
            logger.info("coursesRepository is available and contains objects.");
        }
    }



}
