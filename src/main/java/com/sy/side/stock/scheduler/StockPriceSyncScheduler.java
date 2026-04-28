package com.sy.side.stock.scheduler;

import com.sy.side.stock.application.port.in.SyncStockPriceUseCase;
import com.sy.side.stock.dto.response.StockPriceSyncResponse;
import com.sy.side.stock.util.StockUtil;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockPriceSyncScheduler {

    private final SyncStockPriceUseCase syncStockPriceUseCase;

    /**
     * 주식 가격 동기화
     * 매일 오전 8시 실행
     * - 당일 오전 8시에는 당일 종가 데이터가 없으므로 전 거래일 기준으로 동기화
     */
    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Seoul")
    public void syncDailyStockPrices() {
        String basDt = StockUtil.resolveKrxPriceBaseDate(
                LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        );

        log.info("[STOCK_PRICE_SCHEDULER] start. basDt={}", basDt);

        StockPriceSyncResponse response = syncStockPriceUseCase.syncAll(basDt);

        log.info(
                "[STOCK_PRICE_SCHEDULER] done. basDt={}, requested={}, success={}, fail={}",
                basDt,
                response.getRequestedCount(),
                response.getSuccessCount(),
                response.getFailCount()
        );
    }
}
