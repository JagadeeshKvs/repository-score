package com.jkrc.repositoryscore.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.jkrc.repositoryscore.client.GithubApiClient;
import com.jkrc.repositoryscore.model.GithubRepositoryItem;
import com.jkrc.repositoryscore.model.RepositoryResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "github.api.url=http://localhost:8081")
public class GithubApiClientIntegrationTest {

    private static MockWebServer mockWebServer;

    @Autowired
    private GithubApiClient githubApiClient;

    private RepositoryResponse mockResponses;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(8081);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void initialize() throws IOException {
        mockWebServer.url("/");

        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("github-mock-response.json");
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mockResponses = objectMapper.readValue(inputStream, RepositoryResponse.class);
    }

    @Test
    void testGetRepositoryResponse_returns_expected_size() throws Exception {
        //GIVEN
        RepositoryResponse mockResponse = mockResponses;

        ObjectMapper objectMapper = new ObjectMapper();
        String mockResponseJson = objectMapper.writeValueAsString(mockResponse);

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponseJson)
                .addHeader("Content-Type", "application/json"));
        //WHEN
        RepositoryResponse response = githubApiClient.getRepositoryResponse("2023-01-01", "Java");

        //THEN
        assertNotNull(response);
        assertEquals(30, response.getItems().size());
    }

    @Test
    void testGetRepositoryResponse_matches_elements() throws Exception {
        //GIVEN
        RepositoryResponse mockResponse = mockResponses;

        ObjectMapper objectMapper = new ObjectMapper();
        String mockResponseJson = objectMapper.writeValueAsString(mockResponse);

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponseJson)
                .addHeader("Content-Type", "application/json"));

        //WHEN
        RepositoryResponse response = githubApiClient.getRepositoryResponse("2023-01-01", "Java");

        //THEN
        List<String> repositoryNames = response.getItems().stream()
                .map(GithubRepositoryItem::getFullName)
                .toList();

        List<String> expectedNames = List.of("F45Mbh/XZs9HfoWyNf","2E73Z/ga7PuDI1AC", "SA78h8l/aOYHucA", "Q0JzW/utb9WxVYtv");

        assertTrue(repositoryNames.containsAll(expectedNames));
    }
}