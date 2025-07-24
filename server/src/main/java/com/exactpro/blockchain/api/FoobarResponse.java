package com.exactpro.blockchain.api;

public class FoobarResponse {
    private final String message;

    public FoobarResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
