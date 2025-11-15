package org.gamelog.model;

import org.gamelog.repository.SessionRepo;

public class SessionManager {

    private static SessionManager instance;

    private final String username;

    private final String sessionToken;

    private SessionManager(String username, String sessionToken) {
        this.username = username;
        this.sessionToken = sessionToken;
    }

    // Creates a new session after successful login/signup
    public static void createSession(String username) {
        SessionRepo sessionRepo = new SessionRepo();
        String sessionToken = sessionRepo.createSession(username);
        instance = new SessionManager(username, sessionToken);
    }


    // Initializes session from existing session data for app startup
    public static void createSessionFromExisting(String username, String sessionToken) {
        instance = new SessionManager(username, sessionToken);
    }

    public static SessionManager getInstance() {
        return instance;
    }

    public static void clearSession() {
        if (instance != null) {
            SessionRepo sessionRepo = new SessionRepo();
            sessionRepo.deleteSession(instance.sessionToken);
            instance = null;
        }
    }

    public String getUsername() { return username; }

    public static boolean isActive() {
        return instance != null;
    }
}
