package org.gamelog.model;

public class SessionManager {

    private static SessionManager instance;

    private final String username;

    private SessionManager(String username) {
        this.username = username;
    }

    /** Create a new session after successful login/signup */
    public static void createSession(String username) {
        instance = new SessionManager(username);
    }

    /** Access the current session */
    public static SessionManager getInstance() {
        return instance;
    }

    /** Destroy session on logout */
    public static void clearSession() {
        instance = null;
    }

    public String getUsername() { return username; }

    /** Convenience helper */
    public boolean isActive() {
        return instance != null;
    }
}
