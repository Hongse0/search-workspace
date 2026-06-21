package com.sy.side.stock.dto.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockInvestmentScoreResponse {
    private String srtnCd;
    private String itmsNm;
    private String basDt;
    private int totalScore;
    private int trendScore;
    private int momentumScore;
    private int volatilityRiskScore;
    private int dataReliabilityScore;
    private int portfolioFitScore;
    private String opinion;
    private Metrics metrics;
    private List<String> reasons;

    @Getter
    @Builder
    public static class Metrics {
        private BigDecimal return5d;
        private BigDecimal return20d;
        private BigDecimal return60d;
        private BigDecimal movingAverage5d;
        private BigDecimal movingAverage20d;
        private BigDecimal movingAverage60d;
        private BigDecimal highProximity20d;
        private BigDecimal volumeRatio5dTo20d;
        private BigDecimal volatility20d;
        private int sharpDropCount20d;
        private BigDecimal drawdownFromHigh20d;
        private int tradingDayCount;
        private boolean latestPriceData;
    }
}
