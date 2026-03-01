package com.sy.side.stock.application.port.in;

import com.sy.side.stock.dto.request.BuyStockRequest;

public interface BuyKoreaStockUseCase {
    void buyKorea(BuyStockRequest req);
}
