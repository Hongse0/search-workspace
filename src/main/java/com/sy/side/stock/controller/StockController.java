package com.sy.side.stock.controller;

import com.sy.side.stock.service.StockSyncService;
import com.sy.side.stock.util.StockUtil;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/stocks")
public class StockController {

    private final StockSyncService stockSyncService;

    @PostMapping("/sync/krx")
    public void syncKrx() {
        String basDt = StockUtil.resolveKrxBaseDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        stockSyncService.syncAll(basDt);
    }
}