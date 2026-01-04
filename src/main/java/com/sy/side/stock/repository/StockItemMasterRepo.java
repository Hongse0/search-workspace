package com.sy.side.stock.repository;

import com.sy.side.search.infrastructure.elasticSearch.StockItemMinView;
import com.sy.side.stock.domain.StockItemMaster;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockItemMasterRepo extends JpaRepository<StockItemMaster, Long> {
    List<StockItemMaster> findAllBySrtnCdIn(List<String> codes);

    List<StockItemMinView> findAllActiveMin();
}


