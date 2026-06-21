package com.sy.side.stock.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockInvestmentScoreSyncStartResponse {
    private String jobId;
    private String status;
    private String message;
}
