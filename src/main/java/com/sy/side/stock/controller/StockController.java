package com.sy.side.stock.controller;

import com.sy.side.stock.application.port.in.SyncStockMasterUseCase;
import com.sy.side.stock.application.port.in.SyncStockSearchIndexUseCase;
import com.sy.side.stock.util.StockUtil;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v2/stocks")
public class StockController {

    private final SyncStockMasterUseCase syncStockMasterUseCase;
    private final SyncStockSearchIndexUseCase syncStockSearchIndexUseCase;

    @PostMapping("/sync/krx")
    public SyncStockMasterUseCase.SyncStockMasterResult syncKrx() {
        String basDt = StockUtil.resolveKrxBaseDate(
                LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        );

        return syncStockMasterUseCase.sync(basDt);
    }

    @PostMapping("/sync/es")
    public SyncStockSearchIndexUseCase.SyncStockSearchIndexResult syncEs() {
        String basDt = StockUtil.resolveKrxBaseDate(
                LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        );

        return syncStockSearchIndexUseCase.sync(basDt);
    }
}