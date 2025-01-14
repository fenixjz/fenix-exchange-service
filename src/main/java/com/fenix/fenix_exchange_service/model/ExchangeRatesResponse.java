package com.fenix.fenix_exchange_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ExchangeRatesResponse {

    private boolean success;
    private long timestamp;
    private String base;
    private String date;

    @JsonProperty("rates")
    private Map<String, Double> rates;
}
