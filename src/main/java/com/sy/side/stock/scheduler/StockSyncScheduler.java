package com.sy.side.stock.scheduler;

import com.sy.side.stock.application.port.in.SyncStockMasterUseCase;
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

    private final SyncStockMasterUseCase syncStockMasterUseCase;

    /**
     * KRX 종목 마스터 동기화
     * 월/수/금 오전 7시 실행
     */
    @Scheduled(cron = "0 0 7 ? * MON,WED,FRI", zone = "Asia/Seoul")
    public void syncStockMaster() {
        String basDt = StockUtil.resolveKrxBaseDate(
                LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        );

        log.info("[STOCK_MASTER_SCHEDULER] start. basDt={}", basDt);

        SyncStockMasterUseCase.SyncStockMasterResult result =
                syncStockMasterUseCase.sync(basDt);

        log.info("[STOCK_MASTER_SCHEDULER] done. result={}", result);
    }
}