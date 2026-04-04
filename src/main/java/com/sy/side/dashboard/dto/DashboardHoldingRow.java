package com.sy.side.dashboard.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardHoldingRow {
    private Long accountId;
    private String brokerName;
    private Long stockId;
    private String stockCode;
    private String stockName;
    private String market;
    private BigDecimal quantity;
    private BigDecimal totalBuyAmount;
    private BigDecimal averageBuyPrice;
    private BigDecimal currentPrice;
}