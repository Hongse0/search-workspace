package com.sy.side.snapshot.application.port.out;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public interface SnapshotStockPriceQueryPort {
    Map<Long, BigDecimal> findLatestPriceMap(Set<Long> stockIds);
}
