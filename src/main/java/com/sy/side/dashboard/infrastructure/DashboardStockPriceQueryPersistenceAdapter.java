package com.sy.side.dashboard.infrastructure;

import com.sy.side.dashboard.application.port.out.DashboardStockPriceQueryPort;
import com.sy.side.stock.infrastructure.jpa.StockPriceDailyRepository;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DashboardStockPriceQueryPersistenceAdapter implements DashboardStockPriceQueryPort {

    private final StockPriceDailyRepository stockPriceDailyRepository;

    @Override
    public Map<Long, BigDecimal> findLatestPriceMap(Set<Long> stockIds) {
        return stockPriceDailyRepository.findLatestPrices(stockIds).stream()
                .collect(Collectors.toMap(
                        StockPriceDailyRepository.StockPriceRow::getStockId,
                        row -> row.getClosePrice() == null
                                ? BigDecimal.ZERO
                                : BigDecimal.valueOf(row.getClosePrice())
                ));
    }
}