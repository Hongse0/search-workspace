package com.sy.side.stock.scheduler;

import com.sy.side.stock.application.port.in.SyncStockSearchIndexUseCase;
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

    private final SyncStockSearchIndexUseCase syncStockSearchIndexUseCase;

    /**
     * ES 종목 검색 인덱스 동기화
     * 월/수/금 오전 7시 30분
     */
    @Scheduled(cron = "0 30 7 ? * MON,WED,FRI", zone = "Asia/Seoul")
    public void syncStockSearchIndex() {
        String basDt = StockUtil.resolveKrxBaseDate(
                LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        );

        log.info("[STOCK_SEARCH_INDEX_SCHEDULER] start. basDt={}", basDt);

        var result = syncStockSearchIndexUseCase.sync(basDt);

        log.info("[STOCK_SEARCH_INDEX_SCHEDULER] done. result={}", result);
    }
}
