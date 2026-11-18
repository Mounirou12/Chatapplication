package com.project.controlleur;

import java.time.LocalDate;

public class Session {

    private static int currentUserId = -1;
    private static String currentusername = null;
    private static String currentDisplayName = null;
    private static LocalDate currentLastseen = null;

    public static void setCurrentUser(int id, String username, String displayname, LocalDate lastSeen) {
        currentUserId = id;
        currentusername = username;
        currentDisplayName = displayname;
        currentLastseen = lastSeen;

    }

    public static void clear() {
        currentUserId = -1;
        currentusername = null;
        currentDisplayName = null;
        currentLastseen = null;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static boolean isloggesIn() {
        return currentUserId != -1;
    }

    public static String getCurrentusername() {
        return currentusername;
    }

    public static String getCurrentDisplayName() {
        return currentDisplayName;
    }

    public static LocalDate getCurrentLastseen() {
        return currentLastseen;
    }

}
