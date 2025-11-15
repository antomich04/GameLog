package org.gamelog.model;

public class Session {
    private String username;
    private final String sessionToken;

    public Session(String username, String sessionToken) {
        this.username = username;
        this.sessionToken = sessionToken;
    }

    public String getUsername() { return username; }
    public String getSessionToken() { return sessionToken; }

    public void setUsername(String username) { this.username = username; }
}
