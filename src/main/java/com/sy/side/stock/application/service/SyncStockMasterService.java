package com.sy.side.stock.application.service;

import com.sy.side.search.api.dto.response.KrxListedInfoResponse;
import com.sy.side.stock.application.port.in.SyncStockMasterUseCase;
import com.sy.side.stock.application.port.out.LoadKrxStockItemPort;
import com.sy.side.stock.application.port.out.SaveStockItemMasterPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncStockMasterService implements SyncStockMasterUseCase {

    private final LoadKrxStockItemPort loadKrxStockItemPort;
    private final SaveStockItemMasterPort saveStockItemMasterPort;

    @Override
    public SyncStockMasterResult sync(String basDt) {
        int pageNo = 1;
        int totalSaved = 0;

        KrxListedInfoResponse first = loadKrxStockItemPort.fetch(basDt, pageNo);
        var body = safeBody(first);

        int totalCount = body.totalCount();
        int numOfRows = body.numOfRows();

        if (numOfRows <= 0) {
            throw new IllegalStateException("KRX numOfRows is invalid: " + numOfRows);
        }

        int totalPages = (int) Math.ceil((double) totalCount / numOfRows);

        totalSaved += saveStockItemMasterPort.saveFromResponse(first);

        for (pageNo = 2; pageNo <= totalPages; pageNo++) {
            KrxListedInfoResponse response = loadKrxStockItemPort.fetch(basDt, pageNo);
            totalSaved += saveStockItemMasterPort.saveFromResponse(response);

            if (pageNo % 10 == 0) {
                log.info("[STOCK_MASTER_SYNC] basDt={}, progress={}/{}, saved={}",
                        basDt, pageNo, totalPages, totalSaved);
            }
        }

        log.info("[STOCK_MASTER_SYNC] done. basDt={}, totalCount={}, saved={}, totalPages={}",
                basDt, totalCount, totalSaved, totalPages);

        return new SyncStockMasterResult(
                basDt,
                totalCount,
                totalSaved,
                totalPages
        );
    }

    private KrxListedInfoResponse.Body safeBody(KrxListedInfoResponse res) {
        if (res == null || res.response() == null || res.response().body() == null) {
            throw new IllegalStateException("KRX response/body is null");
        }

        return res.response().body();
    }
}