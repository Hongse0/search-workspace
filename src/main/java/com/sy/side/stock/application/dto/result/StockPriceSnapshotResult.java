package com.sy.side.stock.application.dto.result;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockPriceSnapshotResult {
    private final String basDt;
    private final BigDecimal closePrice;
    private final Long vs;
    private final BigDecimal fltRt;
}
