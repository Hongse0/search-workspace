package com.sy.side.stock.infrastructure.jpa;

import com.sy.side.stock.domain.StockMasterSyncHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMasterSyncHistoryRepository extends JpaRepository<StockMasterSyncHistory, Long> {
}