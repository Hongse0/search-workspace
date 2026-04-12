package com.sy.side.stock.infrastructure.scheduler;

import com.sy.side.stock.application.port.in.SyncStockPriceUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockPriceSyncScheduler {

    private final SyncStockPriceUseCase syncStockPriceUseCase;

    @Scheduled(cron = "0 0 19 * * MON-FRI", zone = "Asia/Seoul")
    public void syncDailyStockPrices() {
        log.info("[StockPriceSyncScheduler] start daily stock price sync");
        var result = syncStockPriceUseCase.syncAll(null);
        log.info("[StockPriceSyncScheduler] done. requested={}, success={}, fail={}",
                result.getRequestedCount(), result.getSuccessCount(), result.getFailCount());
    }
}