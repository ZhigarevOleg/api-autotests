package org.example.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Product(
            int id,
            String title,
            double price
    ) {}
