package com.sy.side.stock.application.port.in;

public interface SyncStockInvestmentScoreUseCase {
    SyncStockInvestmentScoreResult sync();

    String startAsync();

    record SyncStockInvestmentScoreResult(
            int targetCount,
            int savedCount,
            int failCount
    ) {
    }
}
