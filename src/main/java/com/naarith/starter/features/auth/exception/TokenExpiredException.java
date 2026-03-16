package com.naarith.starter.features.auth.exception;

import com.naarith.starter.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TokenExpiredException extends BaseException {
    public TokenExpiredException() {
        super(HttpStatus.UNAUTHORIZED);
    }
}
