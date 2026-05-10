package com.sy.side.stock.scheduler;

import com.sy.side.stock.application.port.in.SyncEtfItemUseCase;
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

    private static final String STOCK_JOB_NAME = "STOCK_MASTER_SYNC";
    private static final String ETF_JOB_NAME = "ETF_MASTER_SYNC";

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final SyncStockMasterUseCase syncStockMasterUseCase;
    private final SyncEtfItemUseCase syncEtfItemUseCase;
    private final StockMasterSyncHistoryRepository stockMasterSyncHistoryRepository;

    /**
     * KRX 단일 주식 종목 마스터 동기화
     * 월/수/금 오전 7시 실행
     */
    @Scheduled(cron = "0 0 7 * * MON,WED,FRI", zone = "Asia/Seoul")
    public void syncStockMaster() {
        String basDt = StockUtil.resolveKrxBaseDate(
                LocalDateTime.now(KST)
        );

        StockMasterSyncHistory history = stockMasterSyncHistoryRepository.save(
                StockMasterSyncHistory.start(STOCK_JOB_NAME, basDt)
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

            log.info("[단일 주식 종목 마스터 스케줄러 성공] basDt={}, totalCount={}, saved={}, totalPages={}",
                    basDt, result.totalCount(), result.saved(), result.totalPages());

        } catch (Exception e) {
            history.fail(e.getMessage());
            stockMasterSyncHistoryRepository.save(history);

            log.error("[단일 주식 종목 마스터 스케줄러 실패] basDt={}", basDt, e);

            throw e;
        }
    }

    /**
     * ETF 종목 마스터 동기화
     * 화/목 오전 7시 실행
     * 전 영업일 기준 데이터 수집
     */
    @Scheduled(cron = "0 0 7 * * TUE,THU", zone = "Asia/Seoul")
    public void syncEtfMaster() {
        String basDt = StockUtil.resolveKrxPriceBaseDate(
                LocalDateTime.now(KST)
        );

        StockMasterSyncHistory history = stockMasterSyncHistoryRepository.save(
                StockMasterSyncHistory.start(ETF_JOB_NAME, basDt)
        );

        try {
            SyncEtfItemUseCase.SyncEtfItemResult result =
                    syncEtfItemUseCase.sync(basDt);

            history.success(
                    result.totalCount(),
                    result.saved(),
                    result.totalPages()
            );

            stockMasterSyncHistoryRepository.save(history);

            log.info("[ETF 종목 마스터 스케줄러 성공] basDt={}, totalCount={}, saved={}, totalPages={}",
                    basDt, result.totalCount(), result.saved(), result.totalPages());

        } catch (Exception e) {
            history.fail(e.getMessage());
            stockMasterSyncHistoryRepository.save(history);

            log.error("[ETF 종목 마스터 스케줄러 실패] basDt={}", basDt, e);

            throw e;
        }
    }
}