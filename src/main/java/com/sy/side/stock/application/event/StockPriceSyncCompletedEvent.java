package com.sy.side.stock.application.event;

import com.sy.side.stock.dto.response.StockPriceSyncResponse;

public record StockPriceSyncCompletedEvent(
        String jobName,
        String basDt,
        Long historyId,
        StockPriceSyncResponse response
) {
}
