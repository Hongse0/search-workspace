package com.sy.side.stock.application.port.in;

import com.sy.side.stock.dto.response.StockPriceSyncResponse;

public interface RunStockPriceSyncJobUseCase {
    StockPriceSyncJobResult run(String jobName, String basDt);

    record StockPriceSyncJobResult(Long historyId, String basDt, StockPriceSyncResponse response) {}
}
