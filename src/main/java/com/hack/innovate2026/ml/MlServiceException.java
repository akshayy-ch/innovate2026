package com.hack.innovate2026.ml;

public class MlServiceException extends RuntimeException {

    private final int statusCode;

    public MlServiceException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
