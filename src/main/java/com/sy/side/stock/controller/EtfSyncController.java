package com.sy.side.stock.controller;

import com.sy.side.stock.application.port.in.SyncEtfItemUseCase;
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
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/stocks/sync")
public class EtfSyncController {

    private final SyncEtfItemUseCase syncEtfItemUseCase;
    private final SyncStockSearchIndexUseCase syncStockSearchIndexUseCase;

    /**
     * ETF 종목 마스터 동기화
     */
    @PostMapping("/etf")
    public SyncEtfItemUseCase.SyncEtfItemResult syncEtf(@RequestParam(required = false) String basDt) {
        String targetBasDt = resolveEtfBasDt(basDt);

        log.info("[ETF 종목 마스터 수동 동기화 요청] basDt={}", targetBasDt);

        return syncEtfItemUseCase.sync(targetBasDt);
    }

    @PostMapping("/etf-to-es")
    public EtfManualBatchResponse syncEtfToEs(@RequestParam(required = false) String basDt) {
        String targetBasDt = resolveEtfBasDt(basDt);

        log.info("[ETF 종목 MySQL 저장 및 ES 이관 수동 배치 요청] basDt={}", targetBasDt);

        SyncEtfItemUseCase.SyncEtfItemResult mysqlResult = syncEtfItemUseCase.sync(targetBasDt);
        SyncStockSearchIndexUseCase.SyncStockSearchIndexResult esResult = syncStockSearchIndexUseCase.sync(targetBasDt);

        return new EtfManualBatchResponse(targetBasDt, mysqlResult, esResult);
    }

    private String resolveEtfBasDt(String basDt) {
        if (basDt != null && !basDt.isBlank()) {
            return basDt;
        }

        return StockUtil.resolveKrxPriceBaseDate(
                LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        );
    }

    public record EtfManualBatchResponse(
            String basDt,
            SyncEtfItemUseCase.SyncEtfItemResult mysql,
            SyncStockSearchIndexUseCase.SyncStockSearchIndexResult es
    ) {
    }
}
