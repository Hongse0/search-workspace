package com.sy.side.stock.application.dto.result;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockInvestmentScoreSummaryResult {
    private final String srtnCd;
    private final String basDt;
    private final Integer totalScore;
    private final String opinion;
}
