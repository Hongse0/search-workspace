package com.sy.side.stock.application.service;

import com.sy.side.stock.application.port.in.SyncStockSearchIndexUseCase;
import com.sy.side.stock.application.port.out.IndexStockItemPort;
import com.sy.side.stock.application.port.out.LoadStockItemMasterPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncStockSearchIndexService implements SyncStockSearchIndexUseCase {

    private final LoadStockItemMasterPort loadStockItemMasterPort;
    private final IndexStockItemPort indexStockItemPort;

    private static final int PER_PAGE = 1000;

    @Override
    @Transactional(readOnly = true)
    public SyncStockSearchIndexResult sync(String basDt) {
        int page = 0;
        long totalIndexed = 0;

        log.info("[ES_SYNC_START] basDt={}", basDt);

        while (true) {
            var pageable = PageRequest.of(page, PER_PAGE);
            var result = loadStockItemMasterPort.findByBasDt(basDt, pageable);

            log.info("[ES_SYNC_PAGE] basDt={}, page={}, fetchedSize={}",
                    basDt, page, result.getContent().size());

            if (result.isEmpty()) {
                break;
            }

            int indexed = indexStockItemPort.bulkUpsert(result.getContent());
            totalIndexed += indexed;

            if (!result.hasNext()) {
                break;
            }

            page++;
        }

        log.info("[ES_SYNC_DONE] basDt={}, totalIndexed={}", basDt, totalIndexed);

        return new SyncStockSearchIndexResult(basDt, totalIndexed);
    }
}