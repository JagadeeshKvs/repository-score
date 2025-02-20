package com.jkrc.repositoryscore.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RepositorySearchCriteria {

    @NotBlank(message = "createdAfter is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "createdAfter must be in format YYYY-MM-DD")
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
