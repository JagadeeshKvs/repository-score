package com.jkrc.repositoryscore.query;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RepositoryQueryBuilderTest {

    @Test
    public void testBuildQueryUrl() {
        //GIVEN
        String baseUrl = "https://api.github.com/search/repositories";
        String createdAfter = "2021-01-01";
        String language = "Java";
        //WHEN
        String url = RepositoryQueryBuilder
                .withBaseUrl(baseUrl)
                .createdAfter(createdAfter)
                .language(language)
                .build();

        //THEN
        assertTrue(url.contains(baseUrl), "URL should contain the base URL");
        assertTrue(url.contains("q=created:>2021-01-01+language:Java"), "URL should contain the query parameters");
        assertTrue(url.contains("sort=stars"), "URL should contain default sort");
        assertTrue(url.contains("order=desc"), "URL should contain default order");
        assertTrue(url.contains("per_page=100"), "URL should contain per page");
        assertEquals(
                "https://api.github.com/search/repositories" +
                        "?q=created:>2021-01-01+language:Java&sort=stars&order=desc&per_page=100", url
        );
    }
}
