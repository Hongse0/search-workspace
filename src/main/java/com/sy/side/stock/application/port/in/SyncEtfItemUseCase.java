package com.sy.side.stock.application.port.in;

public interface SyncEtfItemUseCase {
    SyncEtfItemResult sync(String basDt);
    record SyncEtfItemResult(
            int totalCount,
            int saved,
            int totalPages
    ) {
    }
}