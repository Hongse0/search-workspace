package com.sy.side.trade.application.port.in;

import com.sy.side.trade.dto.request.SellStockRequest;
import jakarta.validation.Valid;

public interface SellStockUseCase {
    void sellKorea(@Valid SellStockRequest req);
}
