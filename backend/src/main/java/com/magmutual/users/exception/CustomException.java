package com.magmutual.users.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
@Data
public class CustomException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String details;
    private HttpStatus status;

    public CustomException(String message, String details, HttpStatus status) {
        super(message);
        this.details = details;
        this.status = status;
    }

    @Override
    public String toString() {
        return super.toString() + (details != null ? " Details: " + details : "") + " Status: " + status;
    }
}

