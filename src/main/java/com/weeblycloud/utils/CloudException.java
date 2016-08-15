package com.weeblycloud.utils;

import com.google.gson.*;

/**
 * Exception indicating the API returned an error.
 */
public class CloudException extends Exception{
    private int code;

    /**
    * @param message The exception's error message.
    * @param code The error code.
    */
    public CloudException(String message, int code) {
        super(message);
        this.code = code;
    }

    /**
    * @param error An error returned in a response from the Weebly Cloud server.
    */
    public CloudException(JsonObject error) {
        super(error.get("message").toString());
        this.code = error.get("code").getAsInt();
    }

    /**
    * Get the error code.
    */
    public int getCode() {
        return this.code;
    }
}