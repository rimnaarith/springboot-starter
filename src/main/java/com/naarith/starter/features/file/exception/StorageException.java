package com.naarith.starter.features.file.exception;

import com.naarith.starter.exception.BaseException;
import org.springframework.http.HttpStatus;

public class StorageException extends BaseException {
    public StorageException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public StorageException(String message, HttpStatus status) {
        super(message, status);
    }
}
