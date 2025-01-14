package com.fenix.fenix_exchange_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ExchangeAmount {

    @JsonProperty("exchange_date")
    private String exchangeDate;

    @JsonProperty("from_currency")
    private String fromCurrency;

    @JsonProperty("to_currency")
    private String toCurrency;

    @JsonProperty("amount")
    private Double amount;
}
