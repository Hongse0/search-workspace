package com.sy.side.stock.application.port.in;

public interface SyncStockSearchIndexUseCase {

    SyncStockSearchIndexResult sync(String basDt);

    record SyncStockSearchIndexResult(
            String basDt,
            long totalIndexed
    ) {}
}