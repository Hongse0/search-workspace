package com.sy.side.dashboard.application.port.out;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public interface DashboardStockPriceQueryPort {
    Map<Long, BigDecimal> findLatestPriceMap(Set<Long> stockIds);
}