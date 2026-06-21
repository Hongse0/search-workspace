package com.sy.side.stock.application.service;

import com.sy.side.stock.application.port.in.GetStockInvestmentScoreUseCase;
import com.sy.side.stock.application.port.in.SyncStockInvestmentScoreUseCase;
import com.sy.side.stock.application.port.out.StockInvestmentScoreCommandPort;
import com.sy.side.stock.application.port.out.StockItemQueryPort;
import com.sy.side.stock.domain.StockItemMaster;
import com.sy.side.stock.dto.response.StockInvestmentScoreResponse;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncStockInvestmentScoreService implements SyncStockInvestmentScoreUseCase {

    private static final DateTimeFormatter JOB_ID_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final StockItemQueryPort stockItemQueryPort;
    private final GetStockInvestmentScoreUseCase getStockInvestmentScoreUseCase;
    private final StockInvestmentScoreCommandPort stockInvestmentScoreCommandPort;
    private final @Qualifier("batchExecutor") Executor batchExecutor;

    @Override
    public String startAsync() {
        String jobId = "investment-score-" + ZonedDateTime.now(KOREA_ZONE_ID).format(JOB_ID_DATE_TIME_FORMATTER);

        batchExecutor.execute(() -> {
            log.info("[STOCK_INVESTMENT_SCORE_SYNC_ASYNC] start. jobId={}", jobId);

            try {
                SyncStockInvestmentScoreResult result = sync();
                log.info(
                        "[STOCK_INVESTMENT_SCORE_SYNC_ASYNC] done. jobId={}, targetCount={}, savedCount={}, failCount={}",
                        jobId,
                        result.targetCount(),
                        result.savedCount(),
                        result.failCount()
                );
            } catch (Exception e) {
                log.error("[STOCK_INVESTMENT_SCORE_SYNC_ASYNC] failed. jobId={}", jobId, e);
            }
        });

        return jobId;
    }

    @Override
    public SyncStockInvestmentScoreResult sync() {
        List<StockItemMaster> stocks = stockItemQueryPort.findAllActive();

        int savedCount = 0;
        int failCount = 0;

        for (StockItemMaster stock : stocks) {
            try {
                StockInvestmentScoreResponse score = getStockInvestmentScoreUseCase.getScore(stock.getSrtnCd());
                stockInvestmentScoreCommandPort.save(score);
                savedCount++;
            } catch (Exception e) {
                failCount++;
                log.warn("[STOCK_INVESTMENT_SCORE_SYNC] fail. srtnCd={}", stock.getSrtnCd(), e);
            }
        }

        return new SyncStockInvestmentScoreResult(stocks.size(), savedCount, failCount);
    }
}
