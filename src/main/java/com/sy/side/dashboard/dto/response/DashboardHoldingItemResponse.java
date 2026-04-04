package com.sy.side.dashboard.dto.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardHoldingItemResponse {
    private Long stockId;
    private String stockCode;
    private String stockName;
    private String market;

    private BigDecimal totalQuantity;
    private BigDecimal totalBuyAmount;
    private BigDecimal averageBuyPrice;
    private BigDecimal currentPrice;
    private BigDecimal evaluationAmount;
    private BigDecimal profitLoss;
    private BigDecimal profitRate;
    private BigDecimal portfolioWeight;

    private List<DashboardHoldingAccountItemResponse> accounts;
}