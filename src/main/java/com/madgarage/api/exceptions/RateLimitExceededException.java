package com.madgarage.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a rate limit is exceeded.
 * Contains the number of seconds the client must wait.
 */
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class RateLimitExceededException extends RuntimeException {
    private final long waitTimeSeconds;

    public RateLimitExceededException(String message, long waitTimeSeconds) {
        super(message);
        this.waitTimeSeconds = waitTimeSeconds;
    }

    public long getWaitTimeSeconds() {
        return waitTimeSeconds;
    }
}
