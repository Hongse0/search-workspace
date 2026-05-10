package com.sy.side.stock.controller;

import com.sy.side.stock.application.port.in.SyncEtfItemUseCase;
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
@RequestMapping("/v2/stocks/sync")
public class EtfSyncController {

    private final SyncEtfItemUseCase syncEtfItemUseCase;

    /**
     * ETF 종목 마스터 동기화
     */
    @PostMapping("/etf")
    public int syncEtf(@RequestParam(required = false) String basDt) {
        String targetBasDt = resolveBasDt(basDt);

        log.info("[ETF 종목 마스터 수동 동기화 요청] basDt={}", targetBasDt);

        return syncEtfItemUseCase.syncEtfItems(targetBasDt);
    }

    private String resolveBasDt(String basDt) {
        if (basDt != null && !basDt.isBlank()) {
            return basDt;
        }

        return StockUtil.resolveKrxBaseDate(
                LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        );
    }
}