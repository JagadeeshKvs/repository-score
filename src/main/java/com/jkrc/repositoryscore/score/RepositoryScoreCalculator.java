package com.jkrc.repositoryscore.score;

import com.jkrc.repositoryscore.model.GithubRepositoryItem;
import com.jkrc.repositoryscore.model.ScoredRepository;
import com.jkrc.repositoryscore.service.GithubRepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class RepositoryScoreCalculator{

    private static final Logger logger = LoggerFactory.getLogger(GithubRepositoryService.class);
    private static final int DAYS_IN_YEAR = 365;
    private static final double DEFAULT_WEIGHT = 0.0;
    private static final double WEIGHT_ONE = 1.0;


    @Cacheable(value = "repositoryScores", key = "#repository.id")
    public ScoredRepository mapScoredRepository(GithubRepositoryItem repository, double score) {
        logger.debug("Scoring repository with name: {}", repository.getFullName());
        ScoredRepository scoredRepo = new ScoredRepository();
        scoredRepo.setName(repository.getFullName());
        scoredRepo.setStargazersCount(repository.getStargazersCount());
        scoredRepo.setForksCount(repository.getForksCount());
        scoredRepo.setUpdatedAt(repository.getUpdatedAt());
        scoredRepo.setScore(score);

        logger.debug(
                "Finished scoring repository with name: {}, score: {}", repository.getFullName(), scoredRepo.getScore()
        );

        return scoredRepo;
    }

    public double calculateScore(GithubRepositoryItem repository, int starGazerSum, int forksSum) {
        double lastUpdatedWeight = getLastUpdatedWeight(repository.getUpdatedAt());
        double normalizedStar = (double) repository.getStargazersCount() / starGazerSum;
        double normalizedForksCount = (double) repository.getForksCount() / forksSum;
        double score = (normalizedStar * 0.5) +
                (normalizedForksCount * 0.3) +
                (lastUpdatedWeight * 0.2);
        return new BigDecimal(score).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private double getLastUpdatedWeight(Date updatedAt) {
        if (updatedAt == null) {
            return DEFAULT_WEIGHT;
        }

        Instant updated = updatedAt.toInstant();
        Instant now = Instant.now();
        double daysSinceLastUpdate = ChronoUnit.DAYS.between(updated, now);

        if (daysSinceLastUpdate <= 0) {
            return WEIGHT_ONE;
        }

        double yearsSinceLastUpdate = Math.max(1.0, daysSinceLastUpdate / DAYS_IN_YEAR);
        return WEIGHT_ONE - daysSinceLastUpdate / (DAYS_IN_YEAR * yearsSinceLastUpdate);
    }
}
