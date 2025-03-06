package voteapp.geostorageservice.controller;

import com.amazonaws.services.glue.model.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import voteapp.geostorageservice.aop.Logging;
import voteapp.geostorageservice.aop.LoggingLevel;
import voteapp.geostorageservice.dto.ErrorResponse;
import voteapp.geostorageservice.exception.BadRequestException;

@RestControllerAdvice
@Logging(level = LoggingLevel.ERROR)
public class ExceptionHandlerController {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequest(BadRequestException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse entityNotFound(EntityNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }
}
