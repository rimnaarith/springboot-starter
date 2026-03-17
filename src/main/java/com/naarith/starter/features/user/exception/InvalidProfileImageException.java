package com.naarith.starter.features.user.exception;

import com.naarith.starter.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidProfileImageException extends BaseException {
    public InvalidProfileImageException(String message){
        super(message, HttpStatus.BAD_REQUEST);
    }
}
