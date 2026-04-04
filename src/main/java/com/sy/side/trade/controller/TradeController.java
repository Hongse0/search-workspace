package com.sy.side.trade.controller;

import com.sy.side.stock.application.facade.StockFacade;
import com.sy.side.stock.dto.request.BuyStockRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/stocks")
public class TradeController {

    private final StockFacade stockFacade;

    @PostMapping("/korea/buy")
    public String buyStock(@Valid @RequestBody BuyStockRequest req) {
        stockFacade.buyKorea(req);
        return "등록 완료";
    }
}