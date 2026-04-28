package com.sy.side.stock.application.port.out;

import com.sy.side.stock.domain.StockItemMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoadStockItemMasterPort {

    Page<StockItemMaster> findByBasDt(String basDt, Pageable pageable);
}