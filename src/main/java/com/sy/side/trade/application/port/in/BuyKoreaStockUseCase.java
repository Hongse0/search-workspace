package com.sy.side.trade.application.port.in;

import com.sy.side.stock.dto.request.BuyStockRequest;

public interface BuyKoreaStockUseCase {
    void buyKorea(BuyStockRequest req);
}
