package com.sy.side.stock.scheduler;

import com.sy.side.stock.application.port.in.SyncStockInvestmentScoreUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockInvestmentScoreScheduler {

    private final SyncStockInvestmentScoreUseCase syncStockInvestmentScoreUseCase;

    /**
     * 투자 의견 점수 캐시 갱신
     * 월-토 오후 1시 실행
     */
    @Scheduled(cron = "0 0 13 * * MON-SAT", zone = "Asia/Seoul")
    public void syncInvestmentScores() {
        log.info("[STOCK_INVESTMENT_SCORE_SCHEDULER] start");

        SyncStockInvestmentScoreUseCase.SyncStockInvestmentScoreResult result =
                syncStockInvestmentScoreUseCase.sync();

        log.info(
                "[STOCK_INVESTMENT_SCORE_SCHEDULER] done. targetCount={}, savedCount={}, failCount={}",
                result.targetCount(),
                result.savedCount(),
                result.failCount()
        );
    }
}
