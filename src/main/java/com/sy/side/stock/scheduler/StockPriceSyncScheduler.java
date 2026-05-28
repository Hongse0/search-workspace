package com.sy.side.stock.scheduler;

import com.sy.side.stock.application.port.in.SyncStockPriceUseCase;
import com.sy.side.stock.application.event.StockPriceSyncCompletedEvent;
import com.sy.side.stock.domain.StockPriceSyncHistory;
import com.sy.side.stock.dto.response.StockPriceSyncResponse;
import com.sy.side.stock.infrastructure.jpa.StockPriceSyncHistoryRepository;
import com.sy.side.stock.util.StockUtil;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockPriceSyncScheduler {

    private static final String JOB_NAME = "STOCK_PRICE_DAILY_SYNC";
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final SyncStockPriceUseCase syncStockPriceUseCase;
    private final StockPriceSyncHistoryRepository stockPriceSyncHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;

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

        StockPriceSyncHistory history = stockPriceSyncHistoryRepository.save(
                StockPriceSyncHistory.start(JOB_NAME, basDt)
        );

        log.info("[STOCK_PRICE_SCHEDULER] start. basDt={}, historyId={}", basDt, history.getId());

        try {
            StockPriceSyncResponse response = syncStockPriceUseCase.syncAll(basDt);

            history.success(
                    response.getRequestedCount(),
                    response.getSuccessCount(),
                    response.getFailCount()
            );

            stockPriceSyncHistoryRepository.save(history);
            eventPublisher.publishEvent(new StockPriceSyncCompletedEvent(
                    JOB_NAME,
                    basDt,
                    history.getId(),
                    response
            ));

            log.info(
                    "[STOCK_PRICE_SCHEDULER] done. basDt={}, historyId={}, requested={}, success={}, fail={}",
                    basDt,
                    history.getId(),
                    response.getRequestedCount(),
                    response.getSuccessCount(),
                    response.getFailCount()
            );
        } catch (Exception e) {
            history.fail(e.getMessage());
            stockPriceSyncHistoryRepository.save(history);

            log.error(
                    "[STOCK_PRICE_SCHEDULER] failed. basDt={}, historyId={}",
                    basDt,
                    history.getId(),
                    e
            );

            throw e;
        }
    }
}
