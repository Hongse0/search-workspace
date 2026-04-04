package com.sy.side.dashboard.application.service;

import com.sy.side.dashboard.application.port.in.GetDashboardHoldingsUseCase;
import com.sy.side.dashboard.application.port.out.DashboardHoldingQueryPort;
import com.sy.side.dashboard.dto.DashboardHoldingRow;
import com.sy.side.dashboard.dto.response.DashboardHoldingAccountItemResponse;
import com.sy.side.dashboard.dto.response.DashboardHoldingItemResponse;
import com.sy.side.dashboard.dto.response.DashboardHoldingsResponse;
import com.sy.side.dashboard.dto.response.DashboardSummaryResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDashboardHoldingsService implements GetDashboardHoldingsUseCase {

    private final DashboardHoldingQueryPort dashboardHoldingQueryPort;

    @Override
    public DashboardHoldingsResponse getHoldings(Long memberId) {
        List<DashboardHoldingRow> rows = dashboardHoldingQueryPort.findHoldingsByMemberId(memberId);

        Map<Long, HoldingAggregation> aggregationMap = new LinkedHashMap<>();

        for (DashboardHoldingRow row : rows) {
            HoldingAggregation agg = aggregationMap.computeIfAbsent(
                    row.getStockId(),
                    stockId -> HoldingAggregation.from(row)
            );

            if (!agg.isInitialized()) {
                agg.initialize(row);
            } else {
                agg.add(row);
            }
        }

        List<DashboardHoldingItemResponse> holdings = new ArrayList<>();
        BigDecimal totalEvaluationAmount = BigDecimal.ZERO;
        BigDecimal totalBuyAmount = BigDecimal.ZERO;

        for (HoldingAggregation agg : aggregationMap.values()) {
            BigDecimal averageBuyPrice = divide(agg.totalBuyAmount, agg.totalQuantity);
            BigDecimal evaluationAmount = multiply(agg.currentPrice, agg.totalQuantity);
            BigDecimal profitLoss = evaluationAmount.subtract(agg.totalBuyAmount);
            BigDecimal profitRate = calculateRate(profitLoss, agg.totalBuyAmount);

            totalEvaluationAmount = totalEvaluationAmount.add(evaluationAmount);
            totalBuyAmount = totalBuyAmount.add(agg.totalBuyAmount);

            holdings.add(DashboardHoldingItemResponse.builder()
                    .stockId(agg.stockId)
                    .stockCode(agg.stockCode)
                    .stockName(agg.stockName)
                    .market(agg.market)
                    .totalQuantity(agg.totalQuantity)
                    .totalBuyAmount(agg.totalBuyAmount)
                    .averageBuyPrice(averageBuyPrice)
                    .currentPrice(agg.currentPrice)
                    .evaluationAmount(evaluationAmount)
                    .profitLoss(profitLoss)
                    .profitRate(profitRate)
                    .portfolioWeight(BigDecimal.ZERO)
                    .accounts(agg.accounts)
                    .build());
        }

        BigDecimal totalProfitLoss = totalEvaluationAmount.subtract(totalBuyAmount);
        BigDecimal totalProfitRate = calculateRate(totalProfitLoss, totalBuyAmount);

        final BigDecimal finalTotalEvaluationAmount = totalEvaluationAmount;

        List<DashboardHoldingItemResponse> weightedHoldings = holdings.stream()
                .map(item -> DashboardHoldingItemResponse.builder()
                        .stockId(item.getStockId())
                        .stockCode(item.getStockCode())
                        .stockName(item.getStockName())
                        .market(item.getMarket())
                        .totalQuantity(item.getTotalQuantity())
                        .totalBuyAmount(item.getTotalBuyAmount())
                        .averageBuyPrice(item.getAverageBuyPrice())
                        .currentPrice(item.getCurrentPrice())
                        .evaluationAmount(item.getEvaluationAmount())
                        .profitLoss(item.getProfitLoss())
                        .profitRate(item.getProfitRate())
                        .portfolioWeight(calculateRate(item.getEvaluationAmount(), finalTotalEvaluationAmount))
                        .accounts(item.getAccounts())
                        .build())
                .sorted(Comparator.comparing(DashboardHoldingItemResponse::getEvaluationAmount).reversed())
                .toList();

        return DashboardHoldingsResponse.builder()
                .summary(DashboardSummaryResponse.builder()
                        .totalBuyAmount(totalBuyAmount)
                        .totalEvaluationAmount(totalEvaluationAmount)
                        .totalProfitLoss(totalProfitLoss)
                        .totalProfitRate(totalProfitRate)
                        .build())
                .holdings(weightedHoldings)
                .build();
    }

    private BigDecimal divide(BigDecimal numerator, BigDecimal denominator) {
        if (denominator == null || denominator.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal multiply(BigDecimal price, BigDecimal quantity) {
        if (price == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return price.multiply(quantity);
    }

    private BigDecimal calculateRate(BigDecimal value, BigDecimal base) {
        if (base == null || base.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return value.multiply(BigDecimal.valueOf(100))
                .divide(base, 2, RoundingMode.HALF_UP);
    }

    private static class HoldingAggregation {
        private boolean initialized;
        private Long stockId;
        private String stockCode;
        private String stockName;
        private String market;
        private BigDecimal currentPrice = BigDecimal.ZERO;
        private BigDecimal totalQuantity = BigDecimal.ZERO;
        private BigDecimal totalBuyAmount = BigDecimal.ZERO;
        private final List<DashboardHoldingAccountItemResponse> accounts = new ArrayList<>();

        static HoldingAggregation from(DashboardHoldingRow row) {
            HoldingAggregation agg = new HoldingAggregation();
            agg.initialize(row);
            return agg;
        }

        boolean isInitialized() {
            return initialized;
        }

        void initialize(DashboardHoldingRow row) {
            this.initialized = true;
            this.stockId = row.getStockId();
            this.stockCode = row.getStockCode();
            this.stockName = row.getStockName();
            this.market = row.getMarket();
            this.currentPrice = defaultZero(row.getCurrentPrice());
            this.totalQuantity = defaultZero(row.getQuantity());
            this.totalBuyAmount = defaultZero(row.getTotalBuyAmount());
            this.accounts.add(toAccountItem(row));
        }

        void add(DashboardHoldingRow row) {
            this.totalQuantity = this.totalQuantity.add(defaultZero(row.getQuantity()));
            this.totalBuyAmount = this.totalBuyAmount.add(defaultZero(row.getTotalBuyAmount()));
            this.accounts.add(toAccountItem(row));
        }

        private DashboardHoldingAccountItemResponse toAccountItem(DashboardHoldingRow row) {
            return DashboardHoldingAccountItemResponse.builder()
                    .accountId(row.getAccountId())
                    .brokerName(row.getBrokerName())
                    .quantity(defaultZero(row.getQuantity()))
                    .averageBuyPrice(defaultZero(row.getAverageBuyPrice()))
                    .buyAmount(defaultZero(row.getTotalBuyAmount()))
                    .build();
        }

        private BigDecimal defaultZero(BigDecimal value) {
            return value == null ? BigDecimal.ZERO : value;
        }
    }
}
