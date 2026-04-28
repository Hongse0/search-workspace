package com.sy.side.stock.infrastructure;

import com.sy.side.stock.application.port.out.LoadStockItemMasterPort;
import com.sy.side.stock.domain.StockItemMaster;
import com.sy.side.stock.repository.StockItemMasterRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockItemMasterQueryAdapter implements LoadStockItemMasterPort {

    private final StockItemMasterRepo stockItemMasterRepo;

    @Override
    public Page<StockItemMaster> findByBasDt(String basDt, Pageable pageable) {
        return stockItemMasterRepo.findByBasDt(basDt, pageable);
    }
}
