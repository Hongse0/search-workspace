package com.sy.side.stock.scheduler;

import com.sy.side.stock.application.port.in.SyncStockMasterUseCase;
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
public class StockSyncScheduler {

    private static final String JOB_NAME = "STOCK_MASTER_SYNC";
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final SyncStockMasterUseCase syncStockMasterUseCase;
    private final StockMasterSyncHistoryRepository stockMasterSyncHistoryRepository;

    /**
     * KRX 종목 마스터 동기화
     * 월/수/금 오전 7시 실행
     */
    @Scheduled(cron = "0 0 7 * * MON,WED,FRI", zone = "Asia/Seoul")
    public void syncStockMaster() {
        String basDt = StockUtil.resolveKrxBaseDate(
                LocalDateTime.now(KST)
        );

        StockMasterSyncHistory history = stockMasterSyncHistoryRepository.save(
                StockMasterSyncHistory.start(JOB_NAME, basDt)
        );

        try {
            SyncStockMasterUseCase.SyncStockMasterResult result =
                    syncStockMasterUseCase.sync(basDt);

            history.success(
                    result.totalCount(),
                    result.saved(),
                    result.totalPages()
            );

            stockMasterSyncHistoryRepository.save(history);
        } catch (Exception e) {
            history.fail(e.getMessage());
            stockMasterSyncHistoryRepository.save(history);
            throw e;
        }
    }
}