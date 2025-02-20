package com.jkrc.repositoryscore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RepositoryResponse {
    private int totalCount;
    private List<GithubRepositoryItem> items;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<GithubRepositoryItem> getItems() {
        return items;
    }

    public void setItems(List<GithubRepositoryItem> repositories) {
        this.items = repositories;
    }

}
