package com.naarith.starter.features.file.exception;

import com.naarith.starter.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidFileException extends BaseException {
    public InvalidFileException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
