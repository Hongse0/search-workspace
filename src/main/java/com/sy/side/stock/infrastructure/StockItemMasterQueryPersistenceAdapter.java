package com.sy.side.stock.infrastructure;

import com.sy.side.stock.application.port.out.StockItemMasterQueryPort;
import com.sy.side.stock.domain.StockItemMaster;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockItemMasterQueryPersistenceAdapter implements StockItemMasterQueryPort {
    @Override
    public StockItemMaster getById(Long stockId) {
        return null;
    }
}
