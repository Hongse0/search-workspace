package com.sy.side.position.infrastructure;

import com.sy.side.position.application.port.out.PositionQueryPort;
import com.sy.side.position.domain.AccountPosition;
import com.sy.side.position.infrastructure.jpa.AccountPositionRepository;
import com.sy.side.stock.infrastructure.jpa.StockPriceDailyRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PositionQueryPersistenceAdapter implements PositionQueryPort {

    private final AccountPositionRepository accountPositionRepository;
    private final StockPriceDailyRepository stockPriceDailyRepository;

    @Override
    public BigDecimal sumStockAssetValueByAccountId(Long accountId) {
        List<AccountPosition> positions = accountPositionRepository.findAllByAccountId(accountId);
        if (positions.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Set<Long> stockIds = positions.stream()
                .map(position -> position.getStock().getId())
                .collect(Collectors.toSet());

        Map<Long, BigDecimal> latestPriceMap = stockPriceDailyRepository.findLatestPrices(stockIds)
                .stream()
                .collect(Collectors.toMap(
                        StockPriceDailyRepository.StockPriceRow::getStockId,
                        row -> row.getClosePrice() == null
                                ? BigDecimal.ZERO
                                : BigDecimal.valueOf(row.getClosePrice())
                ));

        return positions.stream()
                .map(position -> {
                    BigDecimal currentPrice = latestPriceMap.getOrDefault(
                            position.getStock().getId(),
                            BigDecimal.ZERO
                    );
                    long quantity = position.getQuantity() == null ? 0L : position.getQuantity();
                    return currentPrice.multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Long countHoldingByAccountId(Long accountId) {
        Long result = accountPositionRepository.countHoldingByAccountId(accountId);
        return result != null ? result : 0L;
    }
}
