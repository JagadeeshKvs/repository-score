package com.jkrc.repositoryscore.service;

import com.jkrc.repositoryscore.client.GithubApiClient;
import com.jkrc.repositoryscore.model.GithubRepositoryItem;
import com.jkrc.repositoryscore.model.RepositoryResponse;
import com.jkrc.repositoryscore.model.ScoredRepository;
import com.jkrc.repositoryscore.score.RepositoryScoreCalculator;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class GithubRepositoryService {

    private final GithubApiClient repositoryClient;
    private final TaskExecutor taskExecutor;
    private final RepositoryScoreCalculator scoreCalculator;

    private final RestTemplate restTemplate;
    private final Counter repositoryFetchCounter;

    private static final Logger logger = LoggerFactory.getLogger(GithubRepositoryService.class);

    public GithubRepositoryService(
            GithubApiClient repositoryClient,
            TaskExecutor taskExecutor,
            RepositoryScoreCalculator scoreCalculator,
            MeterRegistry meterRegistry) {
        this.repositoryClient = repositoryClient;
        this.taskExecutor = taskExecutor;
        this.scoreCalculator = scoreCalculator;
        this.restTemplate = new RestTemplate();
        this.repositoryFetchCounter = meterRegistry.counter("github.repository.fetch.count");
    }

    @Cacheable(value = "scoredRepositories", key = "#createdAfter + '-' + #language")
    public List<ScoredRepository> fetchAndScoreRepositories(String createdAfter, String language) {
        logger.info("Fetching repositories to score");
        repositoryFetchCounter.increment();
        RepositoryResponse response = repositoryClient.getRepositoryResponse(createdAfter, language);

        if (response == null || response.getItems() == null) {
            return Collections.emptyList();
        }
        logger.info("Fetched {} repositories", response.getItems().size());
        int stargazersSum = response.getItems().stream().mapToInt(GithubRepositoryItem::getStargazersCount).sum();
        int forksSum = response.getItems().stream().mapToInt(GithubRepositoryItem::getForksCount).sum();

        List<CompletableFuture<ScoredRepository>> futures = response.getItems().stream()
                .map(repository ->
                        calculateScoreFor(repository, stargazersSum, forksSum)
                ).toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(ScoredRepository::getScore).reversed())
                .collect(Collectors.toList());
    }

    private CompletableFuture<ScoredRepository> calculateScoreFor(GithubRepositoryItem repository, int stargazersSum, int forksSum) {
        return CompletableFuture.supplyAsync(
                () -> scoreCalculator.calculateScore(repository, stargazersSum, forksSum),
                taskExecutor
        ).thenApply(
                score -> scoreCalculator.mapScoredRepository(repository, score)
        ).exceptionally(ex -> {
            logger.error("Error calculating score for repository {}", repository.getFullName(), ex);
            return null;
        });
    }
}
