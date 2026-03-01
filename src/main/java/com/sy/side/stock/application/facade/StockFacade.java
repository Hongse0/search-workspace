package com.sy.side.stock.application.facade;

import com.sy.side.stock.application.port.in.BuyKoreaStockUseCase;
import com.sy.side.stock.dto.request.BuyStockRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockFacade {

    private final BuyKoreaStockUseCase buyKoreaStockUseCase;

    public void buyKorea(BuyStockRequest req) {
        validateCommon(req);
        buyKoreaStockUseCase.buyKorea(req);
    }

    private void validateCommon(BuyStockRequest req) {
        if (req == null) throw new IllegalArgumentException("req is null");
        if (req.getAccountId() == null) throw new IllegalArgumentException("accountId is required");
    }
}
