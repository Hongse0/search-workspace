package com.sy.side.stock.application.service;

import com.sy.side.stock.application.port.in.SyncEtfItemUseCase;
import com.sy.side.stock.application.port.out.LoadEtfPriceInfoPort;
import com.sy.side.stock.domain.StockItemMaster;
import com.sy.side.stock.dto.response.EtfPriceInfoItem;
import com.sy.side.stock.infrastructure.jpa.StockItemMasterRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EtfItemSyncService implements SyncEtfItemUseCase {

    private final LoadEtfPriceInfoPort loadEtfPriceInfoPort;
    private final StockItemMasterRepository stockItemMasterRepository;

    @Override
    @Transactional
    public int syncEtfItems(String basDt) {
        List<EtfPriceInfoItem> etfItems = loadEtfPriceInfoPort.loadEtfPriceInfos(basDt);

        int savedCount = 0;

        for (EtfPriceInfoItem item : etfItems) {
            if (!hasText(item.getSrtnCd()) || !hasText(item.getItmsNm())) {
                continue;
            }

            StockItemMaster entity = stockItemMasterRepository.findBySrtnCd(item.getSrtnCd())
                    .map(existing -> {
                        existing.updateFromEtf(item);
                        return existing;
                    })
                    .orElseGet(() -> StockItemMaster.fromEtf(item));

            stockItemMasterRepository.save(entity);
            savedCount++;
        }

        log.info("[ETF 종목 마스터 동기화 완료] basDt={}, apiCount={}, savedCount={}",
                basDt, etfItems.size(), savedCount);

        return savedCount;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}