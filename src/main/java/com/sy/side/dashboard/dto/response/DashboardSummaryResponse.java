package com.sy.side.dashboard.dto.response;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardSummaryResponse {
    private BigDecimal totalBuyAmount;
    private BigDecimal totalEvaluationAmount;
    private BigDecimal totalProfitLoss;
    private BigDecimal totalProfitRate;
}
