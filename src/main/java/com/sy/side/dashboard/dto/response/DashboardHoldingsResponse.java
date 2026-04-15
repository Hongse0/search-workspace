package com.sy.side.dashboard.dto.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardHoldingsResponse {

    private Summary summary;
    private List<HoldingItem> holdings;
    private List<RecentTradeItem> recentTrades;

    @Getter
    @Builder
    public static class Summary {
        private BigDecimal totalBuyAmount;
        private BigDecimal totalEvaluationAmount;
        private BigDecimal totalProfitLoss;
        private BigDecimal totalProfitRate;
        private int holdingCount;
    }

    @Getter
    @Builder
    public static class HoldingItem {
        private Long stockId;
        private String stockName;
        private String stockCode;
        private String market;
        private Long quantity;
        private BigDecimal avgBuyPrice;
        private BigDecimal currentPrice;
        private BigDecimal buyAmount;
        private BigDecimal evaluationAmount;
        private BigDecimal profitLoss;
        private BigDecimal profitRate;
    }

    @Getter
    @Builder
    public static class RecentTradeItem {
        private Long tradeId;
        private Long stockId;
        private String stockName;
        private String stockCode;
        private String tradeType;
        private Long quantity;
        private BigDecimal price;
        private BigDecimal totalAmount;
        private String tradedAt;
    }

    public static DashboardHoldingsResponse empty() {
        return DashboardHoldingsResponse.builder()
                .summary(Summary.builder()
                        .totalBuyAmount(BigDecimal.ZERO)
                        .totalEvaluationAmount(BigDecimal.ZERO)
                        .totalProfitLoss(BigDecimal.ZERO)
                        .totalProfitRate(BigDecimal.ZERO)
                        .holdingCount(0)
                        .build())
                .holdings(List.of())
                .recentTrades(List.of())
                .build();
    }
}