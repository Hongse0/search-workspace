package com.sy.side.dashboard.application.service;

import com.sy.side.dashboard.application.port.in.GetDashboardHoldingsUseCase;
import com.sy.side.dashboard.application.port.out.DashboardAccountQueryPort;
import com.sy.side.dashboard.application.port.out.DashboardPositionQueryPort;
import com.sy.side.dashboard.application.port.out.DashboardStockPriceQueryPort;
import com.sy.side.dashboard.application.port.out.DashboardTradeQueryPort;
import com.sy.side.dashboard.dto.response.DashboardHoldingsResponse;
import com.sy.side.stock.application.port.out.StockItemMasterQueryPort;
import com.sy.side.stock.domain.StockItemMaster;
import com.sy.side.trade.dto.AccountPositionSummary;
import com.sy.side.trade.dto.RecentTradeSummary;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDashboardHoldingsService implements GetDashboardHoldingsUseCase {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final int RECENT_TRADE_LIMIT = 5;

    private final DashboardAccountQueryPort dashboardAccountQueryPort;
    private final DashboardPositionQueryPort dashboardPositionQueryPort;
    private final DashboardStockPriceQueryPort dashboardStockPriceQueryPort;
    private final DashboardTradeQueryPort dashboardTradeQueryPort;
    private final StockItemMasterQueryPort stockItemMasterQueryPort;

    @Override
    public DashboardHoldingsResponse getHoldings(Long memberId) {
        List<Long> accountIds = dashboardAccountQueryPort.findAllAccountIdsByMemberId(memberId);

        if (accountIds.isEmpty()) {
            return DashboardHoldingsResponse.empty();
        }

        List<AccountPositionSummary> positions = dashboardPositionQueryPort.findAllByAccountIds(accountIds);
        List<RecentTradeSummary> recentTrades = dashboardTradeQueryPort.findRecentByAccountIds(accountIds, RECENT_TRADE_LIMIT);

        if (positions.isEmpty()) {
            return DashboardHoldingsResponse.builder()
                    .summary(DashboardHoldingsResponse.Summary.builder()
                            .totalBuyAmount(BigDecimal.ZERO)
                            .totalEvaluationAmount(BigDecimal.ZERO)
                            .totalProfitLoss(BigDecimal.ZERO)
                            .totalProfitRate(BigDecimal.ZERO)
                            .holdingCount(0)
                            .build())
                    .holdings(List.of())
                    .recentTrades(toRecentTradeItems(recentTrades))
                    .build();
        }

        Set<Long> stockIds = positions.stream()
                .map(AccountPositionSummary::getStockId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, StockItemMaster> stockMap = stockItemMasterQueryPort.findAllByIds(List.copyOf(stockIds))
                .stream()
                .collect(Collectors.toMap(StockItemMaster::getId, item -> item));

        Map<Long, BigDecimal> currentPriceMap = dashboardStockPriceQueryPort.findLatestPriceMap(stockIds);

        Map<Long, List<AccountPositionSummary>> groupedByStock = positions.stream()
                .collect(Collectors.groupingBy(AccountPositionSummary::getStockId));

        List<DashboardHoldingsResponse.HoldingItem> holdings = groupedByStock.entrySet().stream()
                .map(entry -> toMergedHoldingItem(entry.getKey(), entry.getValue(), stockMap, currentPriceMap))
                .sorted(Comparator.comparing(DashboardHoldingsResponse.HoldingItem::getEvaluationAmount).reversed())
                .toList();

        BigDecimal totalBuyAmount = positions.stream()
                .map(position -> {
                    long quantity = position.getQuantity() == null ? 0L : position.getQuantity();
                    return nullSafe(position.getAvgPrice())
                            .multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEvaluationAmount = sum(holdings.stream()
                .map(DashboardHoldingsResponse.HoldingItem::getEvaluationAmount)
                .toList());

        BigDecimal totalProfitLoss = totalEvaluationAmount.subtract(totalBuyAmount);
        BigDecimal totalProfitRate = calculateRate(totalProfitLoss, totalBuyAmount);

        return DashboardHoldingsResponse.builder()
                .summary(DashboardHoldingsResponse.Summary.builder()
                        .totalBuyAmount(totalBuyAmount)
                        .totalEvaluationAmount(totalEvaluationAmount)
                        .totalProfitLoss(totalProfitLoss)
                        .totalProfitRate(totalProfitRate)
                        .holdingCount(holdings.size())
                        .build())
                .holdings(holdings)
                .recentTrades(toRecentTradeItems(recentTrades))
                .build();
    }

    private DashboardHoldingsResponse.HoldingItem toMergedHoldingItem(
            Long stockId,
            List<AccountPositionSummary> positions,
            Map<Long, StockItemMaster> stockMap,
            Map<Long, BigDecimal> currentPriceMap
    ) {
        StockItemMaster stock = stockMap.get(stockId);

        long totalQuantity = positions.stream()
                .map(AccountPositionSummary::getQuantity)
                .filter(java.util.Objects::nonNull)
                .mapToLong(Long::longValue)
                .sum();

        BigDecimal totalBuyAmount = positions.stream()
                .map(position -> nullSafe(position.getAvgPrice())
                        .multiply(BigDecimal.valueOf(position.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgBuyPrice = totalQuantity > 0
                ? totalBuyAmount.divide(BigDecimal.valueOf(totalQuantity), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal currentPrice = nullSafe(currentPriceMap.get(stockId));
        BigDecimal evaluationAmount = currentPrice.multiply(BigDecimal.valueOf(totalQuantity));
        BigDecimal profitLoss = evaluationAmount.subtract(totalBuyAmount);
        BigDecimal profitRate = calculateRate(profitLoss, totalBuyAmount);

        return DashboardHoldingsResponse.HoldingItem.builder()
                .stockId(stockId)
                .stockName(stock != null ? stock.getItmsNm() : "-")
                .stockCode(stock != null ? stock.getSrtnCd() : "-")
                .market(stock != null ? stock.getMrktCtg() : "-")
                .quantity(totalQuantity)
                .avgBuyPrice(avgBuyPrice)
                .currentPrice(currentPrice)
                .buyAmount(totalBuyAmount)
                .evaluationAmount(evaluationAmount)
                .profitLoss(profitLoss)
                .profitRate(profitRate)
                .build();
    }

    private List<DashboardHoldingsResponse.RecentTradeItem> toRecentTradeItems(List<RecentTradeSummary> recentTrades) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        return recentTrades.stream()
                .map(trade -> DashboardHoldingsResponse.RecentTradeItem.builder()
                        .tradeId(trade.getTradeId())
                        .stockId(trade.getStockId())
                        .stockName(trade.getStockName())
                        .stockCode(trade.getStockCode())
                        .tradeType(trade.getTradeType())
                        .quantity(trade.getQuantity())
                        .price(trade.getPrice())
                        .totalAmount(nullSafe(trade.getPrice()).multiply(
                                BigDecimal.valueOf(trade.getQuantity() == null ? 0L : trade.getQuantity())
                        ))
                        .tradedAt(trade.getTradedAt() != null ? trade.getTradedAt().format(formatter) : null)
                        .tradedAt(trade.getTradedAt().format(formatter))
                        .build())
                .toList();
    }

    private BigDecimal calculateRate(BigDecimal numerator, BigDecimal denominator) {
        if (denominator == null || denominator.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return numerator.multiply(HUNDRED)
                .divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal sum(List<BigDecimal> values) {
        return values.stream()
                .map(this::nullSafe)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}