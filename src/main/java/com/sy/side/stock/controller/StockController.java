package com.sy.side.stock.controller;

import com.sy.side.stock.application.port.in.SyncStockMasterUseCase;
import com.sy.side.stock.application.port.in.SyncStockSearchIndexUseCase;
import com.sy.side.stock.util.StockUtil;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/stocks")
public class StockController {

    private final SyncStockMasterUseCase syncStockMasterUseCase;
    private final SyncStockSearchIndexUseCase syncStockSearchIndexUseCase;

    @PostMapping("/sync/krx")
    public SyncStockMasterUseCase.SyncStockMasterResult syncKrx(@RequestParam(required = false) String basDt) {
        String targetBasDt = resolveStockBasDt(basDt);

        log.info("[주식 종목 마스터 수동 동기화 요청] basDt={}", targetBasDt);

        return syncStockMasterUseCase.sync(targetBasDt);
    }

    @PostMapping("/sync/es")
    public SyncStockSearchIndexUseCase.SyncStockSearchIndexResult syncEs(@RequestParam(required = false) String basDt) {
        String targetBasDt = resolveStockBasDt(basDt);

        log.info("[주식 종목 ES 수동 이관 요청] basDt={}", targetBasDt);

        return syncStockSearchIndexUseCase.sync(targetBasDt);
    }

    @PostMapping("/sync/krx-to-es")
    public StockManualBatchResponse syncKrxToEs(@RequestParam(required = false) String basDt) {
        String targetBasDt = resolveStockBasDt(basDt);

        log.info("[주식 종목 MySQL 저장 및 ES 이관 수동 배치 요청] basDt={}", targetBasDt);

        SyncStockMasterUseCase.SyncStockMasterResult mysqlResult = syncStockMasterUseCase.sync(targetBasDt);
        SyncStockSearchIndexUseCase.SyncStockSearchIndexResult esResult = syncStockSearchIndexUseCase.sync(targetBasDt);

        return new StockManualBatchResponse(targetBasDt, mysqlResult, esResult);
    }

    private String resolveStockBasDt(String basDt) {
        if (basDt != null && !basDt.isBlank()) {
            return basDt;
        }

        return StockUtil.resolveKrxBaseDate(
                LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        );
    }

    public record StockManualBatchResponse(
            String basDt,
            SyncStockMasterUseCase.SyncStockMasterResult mysql,
            SyncStockSearchIndexUseCase.SyncStockSearchIndexResult es
    ) {
    }
}
