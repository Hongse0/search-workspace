package com.sy.side.stock.application.port.in;

public interface SyncStockMasterUseCase {

    SyncStockMasterResult sync(String basDt);

    record SyncStockMasterResult(
            String basDt,
            int totalCount,
            int saved,
            int totalPages
    ) {}
}