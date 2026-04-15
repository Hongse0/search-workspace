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
        Long accountId = dashboardAccountQueryPort.findPrimaryAccountIdByMemberId(memberId)
                .orElse(null);

        if (accountId == null) {
            return DashboardHoldingsResponse.empty();
        }

        List<AccountPositionSummary> positions = dashboardPositionQueryPort.findAllByAccountId(accountId);
        List<RecentTradeSummary> recentTrades = dashboardTradeQueryPort.findRecentByAccountId(accountId, RECENT_TRADE_LIMIT);

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
                .collect(Collectors.toSet());

        Map<Long, StockItemMaster> stockMap = stockItemMasterQueryPort.findAllByIds(List.copyOf(stockIds))
                .stream()
                .collect(Collectors.toMap(StockItemMaster::getId, item -> item));

        Map<Long, BigDecimal> currentPriceMap = dashboardStockPriceQueryPort.findLatestPriceMap(stockIds);

        List<DashboardHoldingsResponse.HoldingItem> holdings = positions.stream()
                .map(position -> toHoldingItem(position, stockMap, currentPriceMap))
                .sorted(Comparator.comparing(DashboardHoldingsResponse.HoldingItem::getEvaluationAmount).reversed())
                .toList();

        BigDecimal totalBuyAmount = sum(holdings.stream()
                .map(DashboardHoldingsResponse.HoldingItem::getBuyAmount)
                .toList());

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

    private DashboardHoldingsResponse.HoldingItem toHoldingItem(
            AccountPositionSummary position,
            Map<Long, StockItemMaster> stockMap,
            Map<Long, BigDecimal> currentPriceMap
    ) {
        StockItemMaster stock = stockMap.get(position.getStockId());

        BigDecimal avgBuyPrice = nullSafe(position.getAvgPrice());
        BigDecimal currentPrice = nullSafe(currentPriceMap.get(position.getStockId()));
        BigDecimal quantity = BigDecimal.valueOf(position.getQuantity());

        BigDecimal buyAmount = avgBuyPrice.multiply(quantity);
        BigDecimal evaluationAmount = currentPrice.multiply(quantity);
        BigDecimal profitLoss = evaluationAmount.subtract(buyAmount);
        BigDecimal profitRate = calculateRate(profitLoss, buyAmount);

        return DashboardHoldingsResponse.HoldingItem.builder()
                .stockId(position.getStockId())
                .stockName(stock != null ? stock.getItmsNm() : "-")
                .stockCode(stock != null ? stock.getSrtnCd() : "-")
                .market(stock != null ? stock.getMrktCtg() : "-")
                .quantity(position.getQuantity())
                .avgBuyPrice(avgBuyPrice)
                .currentPrice(currentPrice)
                .buyAmount(buyAmount)
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
                        .totalAmount(trade.getPrice().multiply(BigDecimal.valueOf(trade.getQuantity())))
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