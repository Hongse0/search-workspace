package com.sy.side.stock.infrastructure;

import com.sy.side.stock.application.port.out.StockItemMasterQueryPort;
import com.sy.side.stock.domain.StockItemMaster;
import com.sy.side.stock.repository.StockItemMasterRepo;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockItemMasterQueryPersistenceAdapter implements StockItemMasterQueryPort {

    private final StockItemMasterRepo stockItemMasterRepo;

    @Override
    public StockItemMaster getById(Long stockId) {
        return stockItemMasterRepo.getReferenceById(stockId);
    }

    @Override
    public Optional<StockItemMaster> findById(Long stockId) {
        return stockItemMasterRepo.findById(stockId);
    }

    @Override
    public Optional<StockItemMaster> findBySrtnCd(String srtnCd) {
        return stockItemMasterRepo.findBySrtnCd(srtnCd);
    }
}