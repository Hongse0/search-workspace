package com.sy.side.trade.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountPositionSummary {
    private Long accountId;
    private Long stockId;
    private Integer quantity;
    private BigDecimal avgPrice;
}
