package com.jkrc.repositoryscore.controller;

import com.jkrc.repositoryscore.model.ScoredRepository;
import com.jkrc.repositoryscore.service.GithubRepositoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RepositoryScoreControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private GithubRepositoryService repositoryScoreService;

    @Test
    public void testGetScoredRepositoriesIntegration() {
        //GIVEN
        ScoredRepository repo = new ScoredRepository();
        repo.setName("integration-repo");
        repo.setScore(88.0);
        when(repositoryScoreService.fetchAndScoreRepositories(anyString(), anyString()))
                .thenReturn(List.of(repo));

        String url = "/api/v1/scoredRepositories?createdAfter=2021-01-01&language=Java";
        //WHEN
        ResponseEntity<ScoredRepository[]> response = restTemplate
                .withBasicAuth("user", "password")
                .getForEntity(url, ScoredRepository[].class);
        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ScoredRepository[] body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.length).isEqualTo(1);
        assertThat(body[0].getName()).isEqualTo("integration-repo");
        assertThat(body[0].getScore()).isEqualTo(88.0);
    }

    @Test
    public void testGetScoredRepositories_incorrect_request_param() {
        //GIVEN
        ScoredRepository repo = new ScoredRepository();
        repo.setName("integration-repo");
        repo.setScore(88.0);
        when(repositoryScoreService.fetchAndScoreRepositories(anyString(), anyString()))
                .thenReturn(List.of(repo));

        //WHEN
        String url = "/api/v1/scoredRepositories?created=2021-01-01&language=Java";
        ResponseEntity<ProblemDetail> response = restTemplate
                .withBasicAuth("user", "password")
                .getForEntity(url, ProblemDetail.class);
        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(Objects.requireNonNull(response.getBody()).getDetail()).contains("Error: createdAfter is required");
    }

    @Test
    public void testGetScoredRepositories_return_401_without_authentication() {
        //GIVEN
        ScoredRepository repo = new ScoredRepository();
        repo.setName("integration-repo");
        repo.setScore(88.0);
        when(repositoryScoreService.fetchAndScoreRepositories(anyString(), anyString()))
                .thenReturn(List.of(repo));

        String url = "/api/v1/scoredRepositories?createdAfter=2021-01-01&language=Java";
        //WHEN
        ResponseEntity<ScoredRepository[]> response = restTemplate
                .getForEntity(url, ScoredRepository[].class);
        //THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
