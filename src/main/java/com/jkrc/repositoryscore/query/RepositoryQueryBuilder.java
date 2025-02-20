package com.jkrc.repositoryscore.query;

import org.springframework.web.util.UriComponentsBuilder;

public class RepositoryQueryBuilder {
    private final String baseUrl;
    private String createdDate;
    private String language;
    private String sort = "stars";
    private String order = "desc";
    private int perPage = 100;

    private RepositoryQueryBuilder(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public static RepositoryQueryBuilder withBaseUrl(String baseUrl){
        return new RepositoryQueryBuilder(baseUrl);
    }

    public RepositoryQueryBuilder createdAfter(String createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public RepositoryQueryBuilder language(String language) {
        this.language = language;
        return this;
    }

    public RepositoryQueryBuilder perPage(int perPage) {
        this.perPage = perPage;
        return this;
    }

    public RepositoryQueryBuilder sortBy(String sort) {
        this.sort = sort;
        return this;
    }

    public RepositoryQueryBuilder order(String order) {
        this.order = order;
        return this;
    }

    public String build() {
        String query = String.format("created:>%s+language:%s", createdDate, language);
        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("q", query)
                .queryParam("sort", sort)
                .queryParam("order", order)
                .queryParam("per_page", perPage)
                .build()
                .toUriString();
    }
}
