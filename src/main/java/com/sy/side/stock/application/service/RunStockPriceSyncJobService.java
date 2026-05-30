package com.sy.side.stock.application.service;

import com.sy.side.stock.application.event.StockPriceSyncCompletedEvent;
import com.sy.side.stock.application.port.in.RunStockPriceSyncJobUseCase;
import com.sy.side.stock.application.port.in.SyncStockPriceUseCase;
import com.sy.side.stock.domain.StockPriceSyncHistory;
import com.sy.side.stock.dto.response.StockPriceSyncResponse;
import com.sy.side.stock.infrastructure.jpa.StockPriceSyncHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RunStockPriceSyncJobService implements RunStockPriceSyncJobUseCase {

    private final SyncStockPriceUseCase syncStockPriceUseCase;
    private final StockPriceSyncHistoryRepository stockPriceSyncHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public StockPriceSyncJobResult run(String jobName, String basDt) {
        StockPriceSyncHistory history = stockPriceSyncHistoryRepository.save(
                StockPriceSyncHistory.start(jobName, basDt)
        );

        log.info("[STOCK_PRICE_SYNC_JOB] start. jobName={}, basDt={}, historyId={}", jobName, basDt, history.getId());

        try {
            StockPriceSyncResponse response = syncStockPriceUseCase.syncAll(basDt);

            history.success(
                    response.getRequestedCount(),
                    response.getSuccessCount(),
                    response.getFailCount()
            );

            stockPriceSyncHistoryRepository.save(history);
            eventPublisher.publishEvent(new StockPriceSyncCompletedEvent(
                    jobName,
                    basDt,
                    history.getId(),
                    response
            ));

            log.info(
                    "[STOCK_PRICE_SYNC_JOB] done. jobName={}, basDt={}, historyId={}, requested={}, success={}, fail={}",
                    jobName,
                    basDt,
                    history.getId(),
                    response.getRequestedCount(),
                    response.getSuccessCount(),
                    response.getFailCount()
            );

            return new StockPriceSyncJobResult(history.getId(), basDt, response);
        } catch (Exception e) {
            history.fail(e.getMessage());
            stockPriceSyncHistoryRepository.save(history);

            log.error(
                    "[STOCK_PRICE_SYNC_JOB] failed. jobName={}, basDt={}, historyId={}",
                    jobName,
                    basDt,
                    history.getId(),
                    e
            );

            throw e;
        }
    }
}
