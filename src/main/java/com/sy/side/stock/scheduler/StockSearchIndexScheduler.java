package com.sy.side.stock.scheduler;

import com.sy.side.stock.application.port.in.SyncStockSearchIndexUseCase;
import com.sy.side.stock.domain.StockMasterSyncHistory;
import com.sy.side.stock.infrastructure.jpa.StockMasterSyncHistoryRepository;
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
public class StockSearchIndexScheduler {

    private static final String JOB_NAME = "STOCK_SEARCH_INDEX_SYNC";
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final SyncStockSearchIndexUseCase syncStockSearchIndexUseCase;
    private final StockMasterSyncHistoryRepository stockMasterSyncHistoryRepository;

    /**
     * ES 종목 검색 인덱스 동기화
     * 월/수/금 오전 7시 30분
     */
    @Scheduled(cron = "0 30 7 ? * MON,WED,FRI", zone = "Asia/Seoul")
    public void syncStockSearchIndex() {
        String basDt = StockUtil.resolveKrxBaseDate(
                LocalDateTime.now(KST)
        );

        StockMasterSyncHistory history = stockMasterSyncHistoryRepository.save(
                StockMasterSyncHistory.start(JOB_NAME, basDt)
        );

        log.info("[STOCK_SEARCH_INDEX_SCHEDULER] start. basDt={}", basDt);

        try {
            var result = syncStockSearchIndexUseCase.sync(basDt);

            history.success(
                    0,
                    0,
                    0
            );

            stockMasterSyncHistoryRepository.save(history);

            log.info("[STOCK_SEARCH_INDEX_SCHEDULER] done. basDt={}, result={}",
                    basDt, result);

        } catch (Exception e) {
            history.fail(e.getMessage());
            stockMasterSyncHistoryRepository.save(history);

            log.error("[STOCK_SEARCH_INDEX_SCHEDULER] fail. basDt={}", basDt, e);

            throw e;
        }
    }
}