package com.sy.side.stock.infrastructure;

import com.sy.side.search.api.dto.response.KrxListedInfoResponse;
import com.sy.side.stock.application.port.out.SaveStockItemMasterPort;
import com.sy.side.stock.domain.StockItemMaster;
import com.sy.side.stock.repository.StockItemMasterRepo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class StockItemMasterPersistenceAdapter implements SaveStockItemMasterPort {

    private final StockItemMasterRepo stockItemMasterRepo;

    @Override
    @Transactional
    public int saveFromResponse(KrxListedInfoResponse res) {
        var body = safeBody(res);
        var itemsWrapper = body.items();

        if (itemsWrapper == null || itemsWrapper.item() == null || itemsWrapper.item().isEmpty()) {
            return 0;
        }

        var items = itemsWrapper.item();

        List<String> codes = items.stream()
                .map(i -> i.srtnCd())
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (codes.isEmpty()) {
            return 0;
        }

        List<StockItemMaster> existing = stockItemMasterRepo.findAllBySrtnCdIn(codes);

        Map<String, StockItemMaster> existingMap = existing.stream()
                .collect(Collectors.toMap(StockItemMaster::getSrtnCd, e -> e));

        List<StockItemMaster> toSave = new ArrayList<>(items.size());

        for (var item : items) {
            String srtnCd = item.srtnCd();

            if (srtnCd == null || srtnCd.isBlank()) {
                continue;
            }

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
}