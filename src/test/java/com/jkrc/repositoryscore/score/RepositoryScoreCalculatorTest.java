package com.jkrc.repositoryscore.score;

import com.jkrc.repositoryscore.model.GithubRepositoryItem;
import com.jkrc.repositoryscore.model.ScoredRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

public class RepositoryScoreCalculatorTest {

    private RepositoryScoreCalculator calculator;
    private GithubRepositoryItem repository;

    @BeforeEach
    public void setup() {
        calculator = new RepositoryScoreCalculator();
        repository = new GithubRepositoryItem();
        repository.setFullName("test-repo");
        repository.setForksCount(10);
        repository.setStargazersCount(20);

        // Set the repository's updated time to 30 days ago.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        repository.setUpdatedAt(cal.getTime());
    }

    @Test
    public void testCalculateScore() {
        //GIVEN
        int starGazerSum = repository.getStargazersCount();
        int forksSum = repository.getForksCount();

        //WHEN
        double score = calculator.calculateScore(repository, starGazerSum, forksSum);
        ScoredRepository scoredRepo = calculator.mapScoredRepository(repository, score);
        //THEN
        assertNotNull(scoredRepo, "Scored repository should not be null");
        assertEquals("test-repo", scoredRepo.getName(), "Repository name should be set correctly");
        assertTrue(scoredRepo.getScore() > 0, "Score should be positive");
    }

    @Test
    public void testCalculateScoreForDifferentDates() {
        //GIVEN
        int starGazerSum = repository.getStargazersCount();
        int forksSum = repository.getForksCount();

        //WHEN
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -3);
        repository.setUpdatedAt(cal.getTime());
        double oldScore = calculator.calculateScore(repository, starGazerSum, forksSum);
        ScoredRepository scoredRepoOld = calculator.mapScoredRepository(repository, oldScore);

        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        repository.setUpdatedAt(cal.getTime());
        double recentScore = calculator.calculateScore(repository, starGazerSum, forksSum);
        ScoredRepository scoredRepoRecent = calculator.mapScoredRepository(repository, recentScore);
        //THEN
        assertTrue(scoredRepoRecent.getScore() > scoredRepoOld.getScore(),
                "Recent update should produce a higher score");
    }
}
