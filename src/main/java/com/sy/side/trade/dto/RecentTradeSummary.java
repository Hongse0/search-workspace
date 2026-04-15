package com.sy.side.trade.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecentTradeSummary {
    private Long tradeId;
    private Long stockId;
    private String stockName;
    private String stockCode;
    private String tradeType;
    private Long quantity;
    private BigDecimal price;
    private LocalDateTime tradedAt;
}