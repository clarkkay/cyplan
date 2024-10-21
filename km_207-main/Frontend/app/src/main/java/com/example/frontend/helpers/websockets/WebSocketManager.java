package com.example.frontend.helpers.websockets;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;


public class WebSocketManager {
    private static WebSocketManager instance;
    private MyWebSocketClient webSocketClient;
    private WebSocketListener webSocketListener;
    private Map<String, MyWebSocketClient> webSocketClients = new HashMap<>();

    private boolean isConnected = false;

    private WebSocketManager() {}

    /**
     * Retrieves a synchronized instance of the WebSocketManager, ensuring that
     * only one instance of the WebSocketManager exists throughout the application.
     * Synchronization ensures thread safety when accessing or creating the instance.
     *
     * @return A synchronized instance of WebSocketManager.
     */
    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    /**
     * Sets the WebSocketListener for this WebSocketManager instance. The WebSocketListener
     * is responsible for handling WebSocket events, such as received messages and errors.
     *
     * @param listener The WebSocketListener to be set for this WebSocketManager.
     */
    public void setWebSocketListener(WebSocketListener listener) {
        this.webSocketListener = listener;
        Log.d("Websocket Listener", "listener initalized");
    }

    /**
     * Removes the currently set WebSocketListener from this WebSocketManager instance.
     * This action effectively disconnects the listener from handling WebSocket events.
     */
    public void removeWebSocketListener() {
        this.webSocketListener = null;
    }

    /**
     * Initiates a WebSocket connection to the specified server URL. This method
     * establishes a connection with the WebSocket server located at the given URL.
     *
     * @param serverUrl The URL of the WebSocket server to connect to.
     * @param type the type of websocket to be stored in the map
     */
    public void connectWebSocket(String type, String serverUrl) {
        try {
            URI serverUri = URI.create(serverUrl);
            MyWebSocketClient client = new MyWebSocketClient(serverUri);
            client.connect();
            webSocketClients.put(type, client);
            isConnected = true;
            Log.d("Websocket Connected", "connected to " + type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a WebSocket message to the connected WebSocket server. This method allows
     * the application to send a message to the server through the established WebSocket
     * connection.
     *
     * @param message The message to be sent to the WebSocket server.
     * @param type the type of websocket to be stored in the map
     */
    public void sendMessage(String type, String message) {
        MyWebSocketClient client = webSocketClients.get(type);
        if (client != null && client.isOpen()) {
            client.send(message);
        }
    }

    /**
     * Disconnects the WebSocket connection, terminating the communication with the
     * WebSocket server.
     */
    public void disconnectWebSocket(String type) {
        MyWebSocketClient client = webSocketClients.get(type);
        if (client != null) {
            client.close();
            webSocketClients.remove(type);
        }
    }

    /**
     * Disconnects all of the websockets in the map
     */
    public void disconnectAllWebSockets() {
        for (MyWebSocketClient client : webSocketClients.values()) {
            if (client != null) {
                client.close();
            }
        }
        webSocketClients.clear(); // Clear the map after disconnecting all clients
        isConnected = false; // Update the connection status
        Log.d("Websocket", "All WebSocket connections disconnected");
    }

    /**
     * Let's us know if websocket is connected
     * @return true if websocket is connected and false otherwise
     */
    public boolean isConnected() {
        return isConnected;
    }



    /**
     * A private inner class that extends WebSocketClient and represents a WebSocket
     * client instance tailored for specific functionalities within the WebSocketManager.
     * This class encapsulates the WebSocketClient and provides custom behavior or
     * handling for WebSocket communication as needed by the application.
     */
    private class MyWebSocketClient extends WebSocketClient {

        private MyWebSocketClient(URI serverUri) {
            super(serverUri);
        }

        /**
         * Called when the WebSocket connection is successfully opened and a handshake
         * with the server has been completed. This method is invoked to handle the event
         * when the WebSocket connection becomes ready for sending and receiving messages.
         *
         * @param handshakedata The ServerHandshake object containing details about the
         *                      handshake with the server.
         */
        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.d("WebSocket", "Connected");
            if (webSocketListener != null) {
                webSocketListener.onWebSocketOpen(handshakedata);
            }
        }

        /**
         * Called when a WebSocket message is received from the server. This method is
         * invoked to handle incoming WebSocket messages and allows the application to
         * process and respond to messages as needed.
         *
         * @param message The WebSocket message received from the server as a string.
         */
        @Override
        public void onMessage(String message) {
            Log.d("WebSocket", "Received comment: " + message);
            if (webSocketListener != null) {
                webSocketListener.onWebSocketMessage(message);
            }
        }

        /**
         * Called when the WebSocket connection is closed, either due to a client request
         * or a server-initiated close. This method is invoked to handle the WebSocket
         * connection closure event and provides details about the closure, such as the
         * closing code, reason, and whether it was initiated remotely.
         *
         * @param code   The WebSocket closing code indicating the reason for closure.
         * @param reason A human-readable explanation for the WebSocket connection closure.
         * @param remote A boolean flag indicating whether the closure was initiated remotely.
         *               'true' if initiated remotely, 'false' if initiated by the client.
         */
        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.d("WebSocket", "Closed");
            Log.d("Websocket", "Closed because code: " + String.valueOf(code) + " for reason: " + reason + ", Remote: " + String.valueOf(remote));
            if (webSocketListener != null) {
                webSocketListener.onWebSocketClose(code, reason, remote);
            }
        }

        /**
         * Called when an error occurs during WebSocket communication. This method is
         * invoked to handle WebSocket-related errors and allows the application to
         * respond to and log error details.
         *
         * @param ex The Exception representing the WebSocket communication error.
         */
        @Override
        public void onError(Exception ex) {
            Log.d("WebSocket", "Error");
            if (webSocketListener != null) {
                webSocketListener.onWebSocketError(ex);
            }
        }
    }


}
