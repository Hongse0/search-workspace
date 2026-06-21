package com.sy.side.stock.application.service;

import com.sy.side.stock.application.port.in.GetStockInvestmentScoreUseCase;
import com.sy.side.stock.application.port.in.SyncStockInvestmentScoreUseCase;
import com.sy.side.stock.application.port.out.StockInvestmentScoreCommandPort;
import com.sy.side.stock.application.port.out.StockItemQueryPort;
import com.sy.side.stock.domain.StockItemMaster;
import com.sy.side.stock.dto.response.StockInvestmentScoreResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncStockInvestmentScoreService implements SyncStockInvestmentScoreUseCase {

    private final StockItemQueryPort stockItemQueryPort;
    private final GetStockInvestmentScoreUseCase getStockInvestmentScoreUseCase;
    private final StockInvestmentScoreCommandPort stockInvestmentScoreCommandPort;

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
