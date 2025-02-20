package com.jkrc.repositoryscore.client;

import com.jkrc.repositoryscore.model.RepositoryResponse;

public interface RepositoryApiClient {
    RepositoryResponse getRepositoryResponse(String createdAfter, String language);
}
