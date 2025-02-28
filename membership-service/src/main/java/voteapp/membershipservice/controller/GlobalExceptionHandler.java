package voteapp.membershipservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
//
//    @ExceptionHandler(AsyncRequestTimeoutException.class)
//    public ResponseEntity<String> handleAsyncRequestTimeoutException(
//            AsyncRequestTimeoutException e, GenericServletWrapper.HttpServletRequest request) {
//        log.warn("Client aborted connection: {}", request.getRequestURI());
//        return ResponseEntity.status(HttpStatus.GONE).body("Client disconnected");
//    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Global exception handler: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
    }
}