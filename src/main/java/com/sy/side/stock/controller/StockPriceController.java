package com.sy.side.stock.controller;

import com.sy.side.stock.application.port.in.SyncStockPriceUseCase;
import com.sy.side.stock.dto.response.StockPriceSyncResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/stocks/prices")
public class StockPriceController {

    private final SyncStockPriceUseCase syncStockPriceUseCase;

    @PostMapping("/sync/{srtnCd}")
    public StockPriceSyncResponse syncSingle(
            @PathVariable String srtnCd,
            @RequestParam(required = false) String basDt
    ) {
        return syncStockPriceUseCase.syncSingle(srtnCd, basDt);
    }

    @PostMapping("/sync")
    public StockPriceSyncResponse syncAll(
            @RequestParam(required = false) String basDt
    ) {
        return syncStockPriceUseCase.syncAll(basDt);
    }
}