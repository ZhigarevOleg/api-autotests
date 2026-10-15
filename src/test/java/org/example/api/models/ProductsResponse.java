package org.example.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
    public record ProductsResponse(
            List<Product> products,
            int total,
            int skip,
            int limit
    ){}

