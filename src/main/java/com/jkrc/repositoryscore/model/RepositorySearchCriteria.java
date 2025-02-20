package com.jkrc.repositoryscore.model;

import com.jkrc.repositoryscore.validation.ValidDate;
import jakarta.validation.constraints.NotBlank;

public class RepositorySearchCriteria {

    @NotBlank(message = "createdAfter is required")
    @ValidDate(message = "createdAfter must be in format YYYY-MM-DD")
    private String createdAfter;

    @NotBlank(message = "language is required")
    private String language;

    public String getCreatedAfter() {
        return createdAfter;
    }

    public void setCreatedAfter(String createdAfter) {
        this.createdAfter = createdAfter;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
