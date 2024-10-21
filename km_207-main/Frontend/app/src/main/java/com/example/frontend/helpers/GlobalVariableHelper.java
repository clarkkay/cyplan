package com.example.frontend.helpers;

public class GlobalVariableHelper {
    private static String address = "10.90.73.79";

    public static String ip = "http://" + address + ":8080";
    public static String websocket_ip = "ws://" + address + ":8080";

    public static String friendViewing;
    public static String lastActivity;
}
