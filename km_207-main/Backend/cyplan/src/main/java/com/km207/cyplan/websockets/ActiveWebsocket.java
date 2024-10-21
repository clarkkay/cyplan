package com.km207.cyplan.websockets;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

@ServerEndpoint("/status/{username}")
@Component
public class ActiveWebsocket {
    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();

    private static Map<String, Session> usernameSessionMap = new Hashtable<>();
    private static Map<String, String> userStatusMap = new Hashtable<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {
        if (usernameSessionMap.containsKey(username)) {
            session.getBasicRemote().sendText("User is already logged in");
            session.close();
        } else {
            // map current session with username
            sessionUsernameMap.put(session, username);

            // map current username with session
            usernameSessionMap.put(username, session);
            //map current username with status
            userStatusMap.put(username, "active");

            //broadcast("{\"active\": \"" + username + "\"}");
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        String username = sessionUsernameMap.get(session);

        //mark user as inactive
        userStatusMap.put(username, "inactive");
        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);
        broadcast("{\"inactive\": \"" + username + "\"}");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        String username = sessionUsernameMap.get(session);
    }
    private void broadcast(String status) {
        sessionUsernameMap.forEach((session, username) -> {
            try {
                session.getBasicRemote().sendText(status);
            } catch (IOException e) {
            }
        });
    }
}