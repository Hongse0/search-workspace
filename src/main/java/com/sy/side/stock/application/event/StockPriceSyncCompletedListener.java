package com.sy.side.stock.application.event;

import com.sy.side.account.application.port.in.RecalculateAccountAssetsUseCase;
import com.sy.side.account.application.port.in.RecalculateAccountAssetsUseCase.RecalculateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockPriceSyncCompletedListener {

    private final RecalculateAccountAssetsUseCase recalculateAccountAssetsUseCase;

    @EventListener
    public void recalculateAccountAssets(StockPriceSyncCompletedEvent event) {
        try {
            RecalculateResult result = recalculateAccountAssetsUseCase.recalculateAllActiveAccounts();

            log.info(
                    "[STOCK_PRICE_SYNC_LISTENER] account asset recalculation done. basDt={}, historyId={}, requested={}, success={}, fail={}",
                    event.basDt(),
                    event.historyId(),
                    result.requestedCount(),
                    result.successCount(),
                    result.failCount()
            );
        } catch (Exception e) {
            log.error(
                    "[STOCK_PRICE_SYNC_LISTENER] account asset recalculation failed. basDt={}, historyId={}",
                    event.basDt(),
                    event.historyId(),
                    e
            );
        }
    }
}
