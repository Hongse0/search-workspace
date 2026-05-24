package com.sy.side.stock.application.service;

import com.sy.side.search.api.dto.response.KrxListedInfoResponse;
import com.sy.side.stock.application.port.in.SyncStockMasterUseCase;
import com.sy.side.stock.application.port.out.LoadKrxStockItemPort;
import com.sy.side.stock.application.port.out.SaveStockItemMasterPort;
import java.util.Objects;
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
        validateHeader(first, basDt, pageNo);

        var body = safeBody(first);

        int totalCount = body.totalCount();
        int numOfRows = body.numOfRows();

        if (totalCount <= 0) {
            throw new IllegalStateException("KRX returned no stock items. basDt=" + basDt
                    + ", totalCount=" + totalCount
                    + ", resultCode=" + first.response().header().resultCode()
                    + ", resultMsg=" + first.response().header().resultMsg());
        }

        if (numOfRows <= 0) {
            throw new IllegalStateException("KRX numOfRows is invalid: " + numOfRows);
        }

        int totalPages = (int) Math.ceil((double) totalCount / numOfRows);

        totalSaved += saveStockItemMasterPort.saveFromResponse(first);

        for (pageNo = 2; pageNo <= totalPages; pageNo++) {
            KrxListedInfoResponse response = loadKrxStockItemPort.fetch(basDt, pageNo);
            validateHeader(response, basDt, pageNo);
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

    private void validateHeader(KrxListedInfoResponse res, String basDt, int pageNo) {
        if (res == null || res.response() == null || res.response().header() == null) {
            throw new IllegalStateException("KRX response/header is null. basDt=" + basDt + ", pageNo=" + pageNo);
        }

        KrxListedInfoResponse.Header header = res.response().header();

        if (!Objects.equals(header.resultCode(), "00")) {
            throw new IllegalStateException("KRX API call failed. resultCode=" + header.resultCode()
                    + ", resultMsg=" + header.resultMsg()
                    + ", basDt=" + basDt
                    + ", pageNo=" + pageNo);
        }
    }
}
