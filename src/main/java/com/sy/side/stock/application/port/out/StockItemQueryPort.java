package com.sy.side.stock.application.port.out;

import com.sy.side.stock.domain.StockItemMaster;
import java.util.List;
import java.util.Optional;

public interface StockItemQueryPort {
    List<StockItemMaster> findAllActive();
    Optional<StockItemMaster> findBySrtnCd(String srtnCd);
}