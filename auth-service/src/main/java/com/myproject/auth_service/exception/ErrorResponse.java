package com.myproject.auth_service.exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private boolean success = false;
    private String message;
    private int status;
    private long timestamp;

    public static ErrorResponse of(int status, String message) {
        ErrorResponse res = new ErrorResponse();
        res.status = status;
        res.message = message;
        res.timestamp = System.currentTimeMillis();
        return res;
    }
}
