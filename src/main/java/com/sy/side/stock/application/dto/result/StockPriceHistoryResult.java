package com.sy.side.stock.application.dto.result;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockPriceHistoryResult {
    private final String basDt;
    private final Long closePrice;
    private final Long vs;
    private final BigDecimal fltRt;
    private final Long highPrice;
    private final Long lowPrice;
    private final Long tradeQuantity;
}
