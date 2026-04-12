package com.sy.side.stock.application.port.in;

import com.sy.side.stock.dto.response.StockPriceSyncResponse;

public interface SyncStockPriceUseCase {
    StockPriceSyncResponse syncSingle(String srtnCd, String basDt);
    StockPriceSyncResponse syncAll(String basDt);
}