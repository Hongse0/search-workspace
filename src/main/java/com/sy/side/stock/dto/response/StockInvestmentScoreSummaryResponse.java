package com.sy.side.stock.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockInvestmentScoreSummaryResponse {
    private String basDt;
    private Integer totalScore;
    private String opinion;
}
