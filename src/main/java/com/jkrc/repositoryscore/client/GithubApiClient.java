package com.jkrc.repositoryscore.client;

import com.jkrc.repositoryscore.model.RepositoryResponse;
import com.jkrc.repositoryscore.query.RepositoryQueryBuilder;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class GithubApiClient implements RepositoryApiClient {

    private final RestTemplate restTemplate;

    @Value("${github.api.url}")
    private String githubApiUrl;

    private final Timer timer;

    public GithubApiClient(RestTemplateBuilder restTemplateBuilder, MeterRegistry meterRegistry) {
        this.restTemplate = restTemplateBuilder
                .additionalInterceptors((request, body, execution) -> {
                    HttpHeaders headers = request.getHeaders();
                    headers.setAccept(Collections.singletonList(MediaType.valueOf("application/vnd.github+json")));
                    return execution.execute(request, body);
                })
                .build();
        this.timer = meterRegistry.timer("github.api.call.time");
    }

    @Override
    public RepositoryResponse getRepositoryResponse(String createdAfter, String language) {
        String url = RepositoryQueryBuilder
                .withBaseUrl(githubApiUrl)
                .createdAfter(createdAfter)
                .perPage(100)
                .language(language)
                .build();
        return timer.record(() ->
                restTemplate.getForObject(url, RepositoryResponse.class)
        );
    }
}
