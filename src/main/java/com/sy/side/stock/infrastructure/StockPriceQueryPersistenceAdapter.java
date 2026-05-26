package com.sy.side.stock.infrastructure;

import com.sy.side.stock.application.port.out.StockPriceQueryPort;
import com.sy.side.stock.infrastructure.jpa.StockPriceDailyRepository;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockPriceQueryPersistenceAdapter implements StockPriceQueryPort {

    private final StockPriceDailyRepository stockPriceDailyRepository;

    @Override
    public Map<String, BigDecimal> findLatestPriceMapBySrtnCd(Set<String> srtnCds) {
        if (srtnCds.isEmpty()) {
            return Map.of();
        }

        return stockPriceDailyRepository.findLatestPricesBySrtnCd(srtnCds).stream()
                .collect(Collectors.toMap(
                        StockPriceDailyRepository.StockPriceByCodeRow::getSrtnCd,
                        row -> row.getClosePrice() == null
                                ? BigDecimal.ZERO
                                : BigDecimal.valueOf(row.getClosePrice())
                ));
    }
}
