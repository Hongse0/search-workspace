package com.sy.side.stock.application.port.out;

import com.sy.side.stock.application.dto.result.StockPriceSnapshotResult;
import java.util.Map;
import java.util.Set;

public interface StockPriceQueryPort {
    Map<String, StockPriceSnapshotResult> findLatestPriceSnapshotMapBySrtnCd(Set<String> srtnCds);
}
