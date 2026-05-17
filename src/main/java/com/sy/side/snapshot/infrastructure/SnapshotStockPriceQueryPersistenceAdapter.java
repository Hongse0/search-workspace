package com.sy.side.snapshot.infrastructure;

import com.sy.side.snapshot.application.port.out.SnapshotStockPriceQueryPort;
import com.sy.side.stock.infrastructure.jpa.StockPriceDailyRepository;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SnapshotStockPriceQueryPersistenceAdapter implements SnapshotStockPriceQueryPort {

    private final StockPriceDailyRepository stockPriceDailyRepository;

    @Override
    public Map<Long, BigDecimal> findLatestPriceMap(Set<Long> stockIds) {
        if (stockIds == null || stockIds.isEmpty()) {
            return Map.of();
        }
        return stockPriceDailyRepository.findLatestPrices(stockIds).stream()
                .collect(Collectors.toMap(
                        StockPriceDailyRepository.StockPriceRow::getStockId,
                        row -> row.getClosePrice() == null
                                ? BigDecimal.ZERO
                                : BigDecimal.valueOf(row.getClosePrice())
                ));
    }
}
