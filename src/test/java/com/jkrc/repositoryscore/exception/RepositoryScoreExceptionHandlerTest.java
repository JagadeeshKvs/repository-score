package com.jkrc.repositoryscore.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RepositoryScoreExceptionHandlerTest {

    private RepositoryScoreExceptionHandler exceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new RepositoryScoreExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/testEndpoint");
    }

    @Test
    void testHandleRuntimeException() {
        RuntimeException ex = new RuntimeException("Test runtime exception");
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleRuntimeException(ex, request);
        assertNotNull(response, "Response should not be null");
        ProblemDetail detail = response.getBody();
        assertNotNull(detail, "ProblemDetail should not be null");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), detail.getStatus());
        assertEquals("Could not score repository", detail.getDetail());
        assertEquals(URI.create("/api/testEndpoint"), detail.getType());
    }

    @Test
    void testHandleUnknownHostException() {
        UnknownHostException ex = new UnknownHostException("Unknown host error");
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleUnknownHostException(ex, request);
        assertNotNull(response);
        ProblemDetail detail = response.getBody();
        assertNotNull(detail);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), detail.getStatus());
        assertEquals("The GitHub API host could not be resolved: Unknown host error", detail.getDetail());
        assertEquals(URI.create("/api/testEndpoint"), detail.getType());
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Foo");
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleIllegalArgumentException(ex, request);
        assertNotNull(response);
        ProblemDetail detail = response.getBody();
        assertNotNull(detail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), detail.getStatus());
        assertEquals("Invalid argument provided: Foo", detail.getDetail());
        assertEquals(URI.create("/api/testEndpoint"), detail.getType());
    }

    @Test
    void testHandleJsonParseException() {
        JsonParseException ex = new JsonParseException(null, "Some Parse error");
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleJsonException(ex, request);
        assertNotNull(response);
        ProblemDetail detail = response.getBody();
        assertNotNull(detail);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), detail.getStatus());
        assertTrue(detail.getDetail().contains("Error parsing JSON response from GitHub API:"));
        assertTrue(detail.getDetail().contains("Parse error"));
        assertEquals(URI.create("/api/testEndpoint"), detail.getType());
    }

    @Test
    void testHandleJsonMappingException() {
        JsonMappingException ex = new JsonMappingException(null, "Mapping issue");
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleJsonException(ex, request);
        assertNotNull(response);
        ProblemDetail detail = response.getBody();
        assertNotNull(detail);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), detail.getStatus());
        assertTrue(detail.getDetail().contains("Error parsing JSON response from GitHub API:"));
        assertTrue(detail.getDetail().contains("Mapping issue"));
        assertEquals(URI.create("/api/testEndpoint"), detail.getType());
    }

    @Test
    void testHandleResourceAccessException() {
        ResponseEntity<String> response = exceptionHandler.handleResourceAccessException(request);
        assertNotNull(response);
        assertEquals(HttpStatus.GATEWAY_TIMEOUT, response.getStatusCode());
    }
}
