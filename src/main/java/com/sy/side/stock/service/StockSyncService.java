package com.sy.side.stock.service;

import com.sy.side.search.api.dto.response.KrxListedInfoResponse;
import com.sy.side.search.infrastructure.krx.client.KrxListedInfoClient;
import com.sy.side.stock.domain.StockItemMaster;
import com.sy.side.stock.repository.StockItemMasterRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class StockSyncService {

    private final KrxListedInfoClient client;
    private final StockItemMasterRepo stockItemMasterRepo;

    /**
     * basDt 기준으로 KRX 종목 마스터 동기화
     */
    public SyncResult syncAll(String basDt) {
        int pageNo = 1;
        int totalSaved = 0;

        KrxListedInfoResponse first = client.fetch(basDt, pageNo);
        var body = safeBody(first);

        int totalCount = body.totalCount();
        int numOfRows = body.numOfRows();
        if (numOfRows <= 0) {
            // todo: 에러impl로 수정 필요
            throw new IllegalStateException("KRX numOfRows is invalid: " + numOfRows);
        }

        int totalPages = (int) Math.ceil((double) totalCount / numOfRows);

        totalSaved += saveFromResponse(first);

        for (pageNo = 2; pageNo <= totalPages; pageNo++) {
            KrxListedInfoResponse res = client.fetch(basDt, pageNo);
            totalSaved += saveFromResponse(res);

            if (pageNo % 10 == 0) {
                log.info("[KRX_SYNC] basDt={}, progress: page {}/{} (saved={})",
                        basDt, pageNo, totalPages, totalSaved);
            }
        }

        log.info("[KRX_SYNC] done. basDt={}, totalCount={}, saved={}", basDt, totalCount, totalSaved);
        return new SyncResult(basDt, totalCount, totalSaved, totalPages);
    }

    /**
     * 페이지 단위 저장(업서트)
     * - 같은 srtnCd가 DB에 있으면 update
     * - 없으면 insert
     */
    @Transactional
    protected int saveFromResponse(KrxListedInfoResponse res) {
        var body = safeBody(res);
        var itemsWrapper = body.items();
        if (itemsWrapper == null || itemsWrapper.item() == null || itemsWrapper.item().isEmpty()) {
            return 0;
        }

        var items = itemsWrapper.item();

        // 이번 페이지의 srtnCd 목록
        List<String> codes = items.stream()
                .map(i -> i.srtnCd())
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (codes.isEmpty()) return 0;

        // 기존 데이터 한 번에 조회 (N+1 방지)
        List<StockItemMaster> existing = stockItemMasterRepo.findAllBySrtnCdIn(codes);
        Map<String, StockItemMaster> existingMap = existing.stream()
                .collect(Collectors.toMap(StockItemMaster::getSrtnCd, e -> e));

        // update or create
        List<StockItemMaster> toSave = new ArrayList<>(items.size());
        for (var item : items) {
            String srtnCd = item.srtnCd();
            if (srtnCd == null || srtnCd.isBlank()) continue;

            StockItemMaster entity = existingMap.get(srtnCd);
            if (entity != null) {
                entity.updateFrom(item);
                toSave.add(entity);
            } else {
                toSave.add(StockItemMaster.from(item));
            }
        }

        stockItemMasterRepo.saveAll(toSave);
        return toSave.size();
    }

    private KrxListedInfoResponse.Body safeBody(KrxListedInfoResponse res) {
        if (res == null || res.response() == null || res.response().body() == null) {
            throw new IllegalStateException("KRX response/body is null");
        }
        return res.response().body();
    }

    public record SyncResult(String basDt, int totalCount, int saved, int totalPages) {}
}
