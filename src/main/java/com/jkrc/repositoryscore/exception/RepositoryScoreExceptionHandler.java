package com.jkrc.repositoryscore.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;

import java.net.URI;
import java.net.UnknownHostException;
import java.util.stream.Collectors;

@ControllerAdvice
public class RepositoryScoreExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryScoreExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        logError(ex);
        ProblemDetail errorDetails = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Could not score repository"
        );
        errorDetails.setType(URI.create(request.getRequestURI()));
        return ResponseEntity.of(errorDetails).build();
    }

    @ExceptionHandler(UnknownHostException.class)
    public ResponseEntity<ProblemDetail> handleUnknownHostException(UnknownHostException ex, HttpServletRequest request) {
        logError(ex);
        ProblemDetail errorDetails = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE, "The GitHub API host could not be resolved: " + ex.getMessage()
        );
        errorDetails.setType(URI.create(request.getRequestURI()));
        return ResponseEntity.of(errorDetails).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        logError(ex);
        ProblemDetail errorDetails = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Invalid argument provided: " + ex.getMessage()
        );
        errorDetails.setType(URI.create(request.getRequestURI()));
        return ResponseEntity.of(errorDetails).build();
    }

    @ExceptionHandler({JsonParseException.class, JsonMappingException.class})
    public ResponseEntity<ProblemDetail> handleJsonException(Exception ex, HttpServletRequest request) {
        logError(ex);
        ProblemDetail errorDetails = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Error parsing JSON response from GitHub API: " + ex.getMessage()
        );
        errorDetails.setType(URI.create(request.getRequestURI()));
        return ResponseEntity.of(errorDetails).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMissingParams(MethodArgumentNotValidException ex, HttpServletRequest request) {
        logError(ex);
        String name = ex.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        ProblemDetail errorDetails = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, String.format("Error: %s", name)
        );
        errorDetails.setType(URI.create(request.getRequestURI()));

        return ResponseEntity.of(errorDetails).build();
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<String> handleResourceAccessException(HttpServletRequest request) {
        ProblemDetail errorDetails = ProblemDetail.forStatusAndDetail(
                HttpStatus.GATEWAY_TIMEOUT, "The request timed out. Please try again later."
        );
        errorDetails.setType(URI.create(request.getRequestURI()));
        return ResponseEntity.of(errorDetails).build();
    }

    private void logError(Exception ex) {
        logger.error("An unexpected error occurred: ", ex);
    }
}
