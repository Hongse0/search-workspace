package com.sy.side.stock.application.port.out;

import com.sy.side.stock.domain.StockItemMaster;
import java.util.List;

public interface IndexStockItemPort {

    int bulkUpsert(List<StockItemMaster> stockItems);
}