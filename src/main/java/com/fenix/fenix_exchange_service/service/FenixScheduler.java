package com.fenix.fenix_exchange_service.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FenixScheduler {

    private final FenixExchangeService exchangeRateService;

    @PostConstruct
    void init() {
        exchangeRateService.saveRates();
    }

    @Scheduled(cron = "0 0 12 * * ?", zone = "UTC")
    void scheduleSaveRates() {
        exchangeRateService.saveRates();
    }
}
