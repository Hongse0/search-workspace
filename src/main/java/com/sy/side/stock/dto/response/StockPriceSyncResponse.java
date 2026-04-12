package com.sy.side.stock.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockPriceSyncResponse {
    private int requestedCount;
    private int successCount;
    private int failCount;
}