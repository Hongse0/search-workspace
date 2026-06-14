package com.sy.side.account.infrastructure;

import com.sy.side.account.application.port.out.AccountStockPriceQueryPort;
import com.sy.side.stock.infrastructure.jpa.StockPriceDailyRepository;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountStockPriceQueryPersistenceAdapter implements AccountStockPriceQueryPort {

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
