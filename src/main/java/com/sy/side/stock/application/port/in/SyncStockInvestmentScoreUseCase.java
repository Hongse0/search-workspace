package com.sy.side.stock.application.port.in;

public interface SyncStockInvestmentScoreUseCase {
    SyncStockInvestmentScoreResult sync();

    record SyncStockInvestmentScoreResult(
            int targetCount,
            int savedCount,
            int failCount
    ) {
    }
}
