package com.sy.side.stock.application.port.out;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public interface StockPriceQueryPort {
    Map<String, BigDecimal> findLatestPriceMapBySrtnCd(Set<String> srtnCds);
}
