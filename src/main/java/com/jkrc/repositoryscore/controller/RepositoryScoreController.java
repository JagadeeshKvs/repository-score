package com.jkrc.repositoryscore.controller;

import com.jkrc.repositoryscore.model.RepositorySearchCriteria;
import com.jkrc.repositoryscore.model.ScoredRepository;
import com.jkrc.repositoryscore.service.GithubRepositoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RepositoryScoreController {

    @Autowired
    private GithubRepositoryService repositoryScoreService;

    @Autowired
    private CacheManager cacheManager;

    private static final Logger logger = LoggerFactory.getLogger(RepositoryScoreController.class);

    @GetMapping("/v1/scoredRepositories")
    public ResponseEntity<List<ScoredRepository>> getScoredRepositories(
            @Valid @ModelAttribute RepositorySearchCriteria searchCriteria
    ) {
        List<ScoredRepository> response = repositoryScoreService.fetchAndScoreRepositories(
                searchCriteria.getCreatedAfter(), searchCriteria.getLanguage()
        );
        return ResponseEntity.ok(response);
    }
}
