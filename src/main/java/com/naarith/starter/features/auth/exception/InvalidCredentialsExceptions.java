package com.naarith.starter.features.auth.exception;

import com.naarith.starter.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsExceptions extends BaseException {
    public InvalidCredentialsExceptions() {
        super("Invalid credentials", HttpStatus.UNAUTHORIZED);
    }
}
