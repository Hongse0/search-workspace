package com.sy.side.stock.infrastructure.jpa;

import com.sy.side.stock.domain.StockPriceSyncHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPriceSyncHistoryRepository extends JpaRepository<StockPriceSyncHistory, Long> {
}
