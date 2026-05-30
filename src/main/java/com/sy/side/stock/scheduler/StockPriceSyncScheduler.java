package com.sy.side.stock.scheduler;

import com.sy.side.stock.application.port.in.RunStockPriceSyncJobUseCase;
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

    private static final String JOB_NAME = "STOCK_PRICE_DAILY_SYNC";
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final RunStockPriceSyncJobUseCase runStockPriceSyncJobUseCase;

    /**
     * 주식 가격 동기화
     * 매일 오전 11시 실행
     * - KRX 데이터가 오전 8시 기준으로 아직 반영되지 않을 수 있어 오전 11시에 전 거래일 기준으로 동기화
     */
    @Scheduled(cron = "0 0 11 * * *", zone = "Asia/Seoul")
    public void syncDailyStockPrices() {
        String basDt = StockUtil.resolveKrxPriceBaseDate(
                LocalDateTime.now(KST)
        );

        runStockPriceSyncJobUseCase.run(JOB_NAME, basDt);
    }
}
