package com.sy.side.stock.infrastructure;

import com.sy.side.stock.application.dto.result.StockPriceHistoryResult;
import com.sy.side.stock.application.dto.result.StockPriceSnapshotResult;
import com.sy.side.stock.application.port.out.StockPriceQueryPort;
import com.sy.side.stock.domain.StockPriceDaily;
import com.sy.side.stock.infrastructure.jpa.StockPriceDailyRepository;
import java.math.BigDecimal;
import java.util.List;
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
    public Map<String, StockPriceSnapshotResult> findLatestPriceSnapshotMapBySrtnCd(Set<String> srtnCds) {
        if (srtnCds.isEmpty()) {
            return Map.of();
        }

        return stockPriceDailyRepository.findLatestPricesBySrtnCd(srtnCds).stream()
                .collect(Collectors.toMap(
                        StockPriceDailyRepository.StockPriceByCodeRow::getSrtnCd,
                        row -> StockPriceSnapshotResult.builder()
                                .basDt(row.getBasDt())
                                .closePrice(row.getClosePrice() == null
                                        ? BigDecimal.ZERO
                                        : BigDecimal.valueOf(row.getClosePrice()))
                                .vs(row.getVs())
                                .fltRt(row.getFltRt())
                                .build()
                ));
    }

    @Override
    public List<StockPriceHistoryResult> findRecentPriceHistory(String srtnCd, int limit) {
        return stockPriceDailyRepository.findTop61BySrtnCdOrderByBasDtDesc(srtnCd).stream()
                .limit(limit)
                .map(this::toHistoryResult)
                .toList();
    }

    @Override
    public String findLatestMarketBasDt() {
        return stockPriceDailyRepository.findLatestMarketBasDt();
    }

    private StockPriceHistoryResult toHistoryResult(StockPriceDaily price) {
        return StockPriceHistoryResult.builder()
                .basDt(price.getBasDt())
                .closePrice(price.getClpr())
                .vs(price.getVs())
                .fltRt(price.getFltRt())
                .highPrice(price.getHipr())
                .lowPrice(price.getLopr())
                .tradeQuantity(price.getTrqu())
                .build();
    }
}
