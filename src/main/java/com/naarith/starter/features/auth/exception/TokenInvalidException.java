package com.naarith.starter.features.auth.exception;

import com.naarith.starter.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TokenInvalidException extends BaseException {
    public TokenInvalidException() {
        super(HttpStatus.UNAUTHORIZED);
    }
}
