package com.sy.side.account.application.port.out;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public interface AccountStockPriceQueryPort {
    Map<Long, BigDecimal> findLatestPriceMap(Set<Long> stockIds);
}
