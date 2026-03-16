package com.naarith.starter.features.user.exception;

import com.naarith.starter.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }
}
