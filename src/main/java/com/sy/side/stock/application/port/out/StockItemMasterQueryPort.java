package com.sy.side.stock.application.port.out;

import com.sy.side.stock.domain.StockItemMaster;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;

public interface StockItemMasterQueryPort {
    StockItemMaster getById(@NotNull(message = "주식을 선택하세요.") Long stockId);
    Optional<StockItemMaster> findById(Long stockId);
}
