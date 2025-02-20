package com.jkrc.repositoryscore.service;

import com.jkrc.repositoryscore.client.GithubApiClient;
import com.jkrc.repositoryscore.model.GithubRepositoryItem;
import com.jkrc.repositoryscore.model.RepositoryResponse;
import com.jkrc.repositoryscore.model.ScoredRepository;
import com.jkrc.repositoryscore.score.RepositoryScoreCalculator;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.task.TaskExecutor;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GithubRepositoryServiceTest {

    private GithubApiClient repositoryClient;
    private TaskExecutor taskExecutor;
    private RepositoryScoreCalculator scoreCalculator;
    private SimpleMeterRegistry meterRegistry;
    private GithubRepositoryService githubRepositoryService;

    @BeforeEach
    public void setup() {
        repositoryClient = mock(GithubApiClient.class);
        scoreCalculator = mock(RepositoryScoreCalculator.class);
        taskExecutor = Runnable::run;
        meterRegistry = new SimpleMeterRegistry();
        githubRepositoryService = new GithubRepositoryService(repositoryClient, taskExecutor, scoreCalculator, meterRegistry);
    }

    @Test
    public void testFetchAndScoreRepositories_NullResponse() {
        //GIVEN
        when(repositoryClient.getRepositoryResponse(anyString(), anyString())).thenReturn(null);
        //WHEN
        List<ScoredRepository> result = githubRepositoryService.fetchAndScoreRepositories("2021-01-01", "Java");
        //THEN
        assertTrue(result.isEmpty(), "Expected empty list if response is null");
    }

    @Test
    public void testFetchAndScoreRepositories_NullItems() {
        //GIVEN
        RepositoryResponse response = mock(RepositoryResponse.class);
        when(response.getItems()).thenReturn(null);
        when(repositoryClient.getRepositoryResponse(anyString(), anyString())).thenReturn(response);
        //WHEN
        List<ScoredRepository> result = githubRepositoryService.fetchAndScoreRepositories("2021-01-01", "Java");
        //THEN
        assertTrue(result.isEmpty(), "Expected empty list if items are null");
    }

    @Test
    public void testFetchAndScoreRepositories_Success() {
        //GIVEN
        GithubRepositoryItem repo1 = mock(GithubRepositoryItem.class);
        GithubRepositoryItem repo2 = mock(GithubRepositoryItem.class);
        when(repo1.getFullName()).thenReturn("repo1");
        when(repo2.getFullName()).thenReturn("repo2");

        RepositoryResponse response = new RepositoryResponse();
        response.setItems(List.of(repo1, repo2));
        when(repositoryClient.getRepositoryResponse(anyString(), anyString())).thenReturn(response);

        ScoredRepository scoredRepo1 = new ScoredRepository();
        scoredRepo1.setName("repo1");
        scoredRepo1.setScore(80.0);
        ScoredRepository scoredRepo2 = new ScoredRepository();
        scoredRepo2.setName("repo2");
        scoredRepo2.setScore(100.0);

        when(scoreCalculator.mapScoredRepository(eq(repo1), anyDouble())).thenReturn(scoredRepo1);
        when(scoreCalculator.mapScoredRepository(eq(repo2), anyDouble())).thenReturn(scoredRepo2);

        //WHEN
        List<ScoredRepository> result = githubRepositoryService.fetchAndScoreRepositories("2021-01-01", "Java");
        //THEN
        assertEquals(2, result.size(), "Expected two scored repositories");
        assertEquals("repo2", result.get(0).getName(), "Expected repo2 with highest score first");
        assertEquals("repo1", result.get(1).getName(), "Expected repo1 with lower score second");
    }

    @Test
    public void testFetchAndScoreRepositories_ScoreCalculationException() {
        //GIVEN
        GithubRepositoryItem repo1 = mock(GithubRepositoryItem.class);
        GithubRepositoryItem repo2 = mock(GithubRepositoryItem.class);
        when(repo1.getFullName()).thenReturn("newRepo1");
        when(repo2.getFullName()).thenReturn("newRepo2");

        RepositoryResponse response = new RepositoryResponse();
        response.setItems(List.of(repo1, repo2));
        when(repositoryClient.getRepositoryResponse(anyString(), anyString())).thenReturn(response);

        when(scoreCalculator.calculateScore(repo1, 10, 10)).thenThrow(new RuntimeException("Scoring failed"));
        ScoredRepository scoredRepo2 = new ScoredRepository();
        scoredRepo2.setName("newRepo2");
        scoredRepo2.setScore(50.0);
        when(scoreCalculator.mapScoredRepository(eq(repo2), anyDouble())).thenReturn(scoredRepo2);

        //WHEN
        List<ScoredRepository> result = githubRepositoryService.fetchAndScoreRepositories("2021-01-01", "Java");
       //THEN
        assertEquals(1, result.size(), "Expected only one scored repository after exception");
        assertEquals("newRepo2", result.getFirst().getName(), "Expected repo2 to be present in the results");
    }

    @Test
    public void testRepositoryFetchCounterIncrement() {
        //GIVEN
        RepositoryResponse response = new RepositoryResponse();
        response.setItems(Collections.emptyList());
        when(repositoryClient.getRepositoryResponse(anyString(), anyString())).thenReturn(response);
        double initialCount = meterRegistry.counter("github.repository.fetch.count").count();
        //WHEN
        githubRepositoryService.fetchAndScoreRepositories("2021-01-01", "Java");
        double finalCount = meterRegistry.counter("github.repository.fetch.count").count();
        //THEN
        assertEquals(initialCount + 1, finalCount, 0.0001, "Expected counter to increment by 1");
    }
}
