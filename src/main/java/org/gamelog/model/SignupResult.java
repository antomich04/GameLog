package org.gamelog.model;

public class SignupResult {
    private boolean success;
    private String message;

    public SignupResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}