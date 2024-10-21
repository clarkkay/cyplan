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

@ServerEndpoint("/courseComments/{courseCode}")
@Component
public class CourseCommentWebsocket {
    @Autowired
    CoursesRepository coursesRepository;

    // Store all socket session and their corresponding username
    // Two maps for the ease of retrieval by key
    private static Map<Session, String > sessionCourseCodeMap = new Hashtable< >();
    private static Map < String, Session > courseCodeSessionMap = new Hashtable < > ();

    // server side logger
    private final Logger logger = LoggerFactory.getLogger(CourseCommentWebsocket.class);

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
     * @param userComment The comment you received from the user.
     */
    @OnMessage //TODO: call this "onComment"
    public void onMessage(Session session, String userComment) throws IOException {

        // get the username by session
        String courseCode = sessionCourseCodeMap.get(session);

        // server side log
        logger.info("[onMessage] " + courseCode + ": " + userComment);
        broadcast("{\"comment\": \"" + userComment + "\"}");
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
     * @param userComment The comment to be shown to all users.
     */
    private void broadcast(String userComment) {
        sessionCourseCodeMap.forEach((session, courseCode) -> {
            try {
                session.getBasicRemote().sendText(userComment); //this is the line that sends information to the front end
            } catch (IOException e) {
                logger.info("[Broadcast Exception] " + e.getMessage());
            }
        });
    }
}
