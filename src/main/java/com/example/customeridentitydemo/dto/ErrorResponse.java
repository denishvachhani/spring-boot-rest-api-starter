package com.example.customeridentitydemo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private HttpStatus status;
    private String error;
    private String message;
    private List<String> details;

    public ErrorResponse(HttpStatus status, String message, List<String> details) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = status.getReasonPhrase();
        this.message = message;
        this.details = details;
    }
}
