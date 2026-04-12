package com.sy.side.stock.infrastructure;

import com.sy.side.stock.application.port.out.StockItemQueryPort;
import com.sy.side.stock.domain.StockItemMaster;
import com.sy.side.stock.infrastructure.jpa.StockItemMasterRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockItemQueryPersistenceAdapter implements StockItemQueryPort {

    private final StockItemMasterRepository stockItemMasterRepository;

    @Override
    public List<StockItemMaster> findAllActive() {
        return stockItemMasterRepository.findAllByActiveYn("Y");
    }

    @Override
    public Optional<StockItemMaster> findBySrtnCd(String srtnCd) {
        return stockItemMasterRepository.findBySrtnCd(srtnCd);
    }
}
