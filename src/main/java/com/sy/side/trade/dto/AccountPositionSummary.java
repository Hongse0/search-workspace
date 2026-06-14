package com.sy.side.trade.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AccountPositionSummary {
    private Long accountId;
    private Long stockId;
    private Long quantity;
    private BigDecimal avgPrice;
    private String symbolCode;
    private String symbolName;
    private BigDecimal currentPrice = BigDecimal.ZERO;
    private BigDecimal buyAmount = BigDecimal.ZERO;
    private BigDecimal evaluationAmount = BigDecimal.ZERO;
    private BigDecimal profitLoss = BigDecimal.ZERO;
    private BigDecimal profitRate = BigDecimal.ZERO;
    private BigDecimal rate = BigDecimal.ZERO;

    public AccountPositionSummary(
            Long accountId,
            Long stockId,
            Long quantity,
            BigDecimal avgPrice,
            String symbolCode,
            String symbolName
    ) {
        this.accountId = accountId;
        this.stockId = stockId;
        this.quantity = quantity;
        this.avgPrice = avgPrice;
        this.symbolCode = symbolCode;
        this.symbolName = symbolName;
    }
}
