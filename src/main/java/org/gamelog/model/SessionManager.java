package org.gamelog.model;

import org.gamelog.repository.SessionRepo;
import org.gamelog.utils.DeviceUtils;

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
        SessionRepo repo = new SessionRepo();
        String deviceId = DeviceUtils.getDeviceId();
        String token = repo.createSession(username, deviceId);
        instance = new SessionManager(username, token);
    }


    // Initializes session from existing session data for app startup
    public static void createSessionFromExisting(String username, String token) {
        instance = new SessionManager(username, token);
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
