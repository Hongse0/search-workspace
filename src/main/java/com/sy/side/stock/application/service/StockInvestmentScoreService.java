package com.sy.side.stock.application.service;

import com.sy.side.common.exception.BizException;
import com.sy.side.stock.application.dto.result.StockPriceHistoryResult;
import com.sy.side.stock.application.port.in.GetStockInvestmentScoreUseCase;
import com.sy.side.stock.application.port.out.StockItemQueryPort;
import com.sy.side.stock.application.port.out.StockPriceQueryPort;
import com.sy.side.stock.domain.StockItemMaster;
import com.sy.side.stock.dto.response.StockInvestmentScoreResponse;
import com.sy.side.stock.error.StockErrorImpl;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockInvestmentScoreService implements GetStockInvestmentScoreUseCase {

    private static final int PRICE_HISTORY_LIMIT = 61;
    private static final int FIXED_PORTFOLIO_FIT_SCORE = 10;
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final StockItemQueryPort stockItemQueryPort;
    private final StockPriceQueryPort stockPriceQueryPort;

    @Override
    public StockInvestmentScoreResponse getScore(String srtnCd) {
        StockItemMaster stock = stockItemQueryPort.findBySrtnCd(srtnCd)
                .orElseThrow(() -> new BizException(StockErrorImpl.STOCK_NOT_FOUND));
        List<StockPriceHistoryResult> prices =
                stockPriceQueryPort.findRecentPriceHistory(srtnCd, PRICE_HISTORY_LIMIT);

        if (prices.isEmpty()) {
            return emptyScore(stock);
        }

        StockPriceHistoryResult latest = prices.get(0);
        boolean latestMarketData = isLatestMarketData(latest);
        ScoreMetrics metrics = calculateMetrics(prices, latest, latestMarketData);

        int trendScore = calculateTrendScore(metrics);
        int momentumScore = calculateMomentumScore(metrics);
        int volatilityRiskScore = calculateVolatilityRiskScore(metrics);
        int dataReliabilityScore = calculateDataReliabilityScore(prices, latestMarketData);
        int totalScore = trendScore
                + momentumScore
                + volatilityRiskScore
                + dataReliabilityScore
                + FIXED_PORTFOLIO_FIT_SCORE;

        return StockInvestmentScoreResponse.builder()
                .srtnCd(stock.getSrtnCd())
                .itmsNm(stock.getItmsNm())
                .basDt(latest.getBasDt())
                .totalScore(totalScore)
                .trendScore(trendScore)
                .momentumScore(momentumScore)
                .volatilityRiskScore(volatilityRiskScore)
                .dataReliabilityScore(dataReliabilityScore)
                .portfolioFitScore(FIXED_PORTFOLIO_FIT_SCORE)
                .opinion(toOpinion(totalScore))
                .metrics(toResponseMetrics(metrics, prices.size(), latestMarketData))
                .reasons(buildReasons(metrics, dataReliabilityScore))
                .build();
    }

    private StockInvestmentScoreResponse emptyScore(StockItemMaster stock) {
        int totalScore = FIXED_PORTFOLIO_FIT_SCORE;

        return StockInvestmentScoreResponse.builder()
                .srtnCd(stock.getSrtnCd())
                .itmsNm(stock.getItmsNm())
                .totalScore(totalScore)
                .trendScore(0)
                .momentumScore(0)
                .volatilityRiskScore(0)
                .dataReliabilityScore(0)
                .portfolioFitScore(FIXED_PORTFOLIO_FIT_SCORE)
                .opinion(toOpinion(totalScore))
                .metrics(StockInvestmentScoreResponse.Metrics.builder()
                        .tradingDayCount(0)
                        .latestPriceData(false)
                        .build())
                .reasons(List.of("가격 데이터가 없어 가격 기반 점수는 계산하지 않았습니다."))
                .build();
    }

    private ScoreMetrics calculateMetrics(
            List<StockPriceHistoryResult> prices,
            StockPriceHistoryResult latest,
            boolean latestMarketData
    ) {
        BigDecimal currentPrice = toBigDecimal(latest.getClosePrice());

        return ScoreMetrics.builder()
                .currentPrice(currentPrice)
                .return5d(returnRate(currentPrice, closePriceAt(prices, 5)))
                .return20d(returnRate(currentPrice, closePriceAt(prices, 20)))
                .return60d(returnRate(currentPrice, closePriceAt(prices, 60)))
                .movingAverage5d(movingAverage(prices, 5))
                .movingAverage20d(movingAverage(prices, 20))
                .movingAverage60d(movingAverage(prices, 60))
                .highProximity20d(highProximity(prices, currentPrice, 20))
                .volumeRatio5dTo20d(volumeRatio(prices))
                .volatility20d(volatility(prices, 20))
                .sharpDropCount20d(sharpDropCount(prices, 20))
                .drawdownFromHigh20d(drawdownFromHigh(prices, currentPrice, 20))
                .latestPriceData(latestMarketData)
                .build();
    }

    private int calculateTrendScore(ScoreMetrics metrics) {
        int score = 0;
        score += scoreReturn(metrics.getReturn5d(), 3, 7, 7);
        score += scoreReturn(metrics.getReturn20d(), 5, 12, 8);
        score += scoreReturn(metrics.getReturn60d(), 10, 25, 8);
        score += isAbove(metrics.getCurrentPrice(), metrics.getMovingAverage5d()) ? 2 : 0;
        score += isAbove(metrics.getCurrentPrice(), metrics.getMovingAverage20d()) ? 2 : 0;
        score += isAbove(metrics.getCurrentPrice(), metrics.getMovingAverage60d()) ? 3 : 0;
        return Math.min(score, 30);
    }

    private int calculateMomentumScore(ScoreMetrics metrics) {
        int score = 0;

        BigDecimal highProximity = metrics.getHighProximity20d();
        if (gte(highProximity, "98")) {
            score += 7;
        } else if (gte(highProximity, "95")) {
            score += 5;
        } else if (gte(highProximity, "90")) {
            score += 3;
        }

        BigDecimal volumeRatio = metrics.getVolumeRatio5dTo20d();
        if (gte(volumeRatio, "1.5")) {
            score += 6;
        } else if (gte(volumeRatio, "1.2")) {
            score += 4;
        } else if (gte(volumeRatio, "1.0")) {
            score += 2;
        }

        BigDecimal return5d = metrics.getReturn5d();
        if (gte(return5d, "5")) {
            score += 7;
        } else if (gte(return5d, "2")) {
            score += 5;
        } else if (gte(return5d, "0")) {
            score += 3;
        }

        return Math.min(score, 20);
    }

    private int calculateVolatilityRiskScore(ScoreMetrics metrics) {
        int score = 0;

        BigDecimal volatility = metrics.getVolatility20d();
        if (volatility != null) {
            if (lte(volatility, "2")) {
                score += 8;
            } else if (lte(volatility, "4")) {
                score += 5;
            } else if (lte(volatility, "6")) {
                score += 2;
            }
        }

        int dropCount = metrics.getSharpDropCount20d();
        if (dropCount == 0) {
            score += 6;
        } else if (dropCount <= 2) {
            score += 3;
        }

        BigDecimal drawdown = metrics.getDrawdownFromHigh20d();
        if (drawdown != null) {
            if (lte(drawdown, "5")) {
                score += 6;
            } else if (lte(drawdown, "12")) {
                score += 3;
            } else if (lte(drawdown, "20")) {
                score += 1;
            }
        }

        return Math.min(score, 20);
    }

    private int calculateDataReliabilityScore(List<StockPriceHistoryResult> prices, boolean latestMarketData) {
        int score = 0;
        if (latestMarketData) {
            score += 5;
        }

        int tradingDayCount = prices.size();
        if (tradingDayCount >= 60) {
            score += 5;
        } else if (tradingDayCount >= 20) {
            score += 3;
        } else if (tradingDayCount >= 5) {
            score += 1;
        }

        return score;
    }

    private StockInvestmentScoreResponse.Metrics toResponseMetrics(
            ScoreMetrics metrics,
            int tradingDayCount,
            boolean latestPriceData
    ) {
        return StockInvestmentScoreResponse.Metrics.builder()
                .return5d(scale(metrics.getReturn5d()))
                .return20d(scale(metrics.getReturn20d()))
                .return60d(scale(metrics.getReturn60d()))
                .movingAverage5d(scale(metrics.getMovingAverage5d()))
                .movingAverage20d(scale(metrics.getMovingAverage20d()))
                .movingAverage60d(scale(metrics.getMovingAverage60d()))
                .highProximity20d(scale(metrics.getHighProximity20d()))
                .volumeRatio5dTo20d(scale(metrics.getVolumeRatio5dTo20d()))
                .volatility20d(scale(metrics.getVolatility20d()))
                .sharpDropCount20d(metrics.getSharpDropCount20d())
                .drawdownFromHigh20d(scale(metrics.getDrawdownFromHigh20d()))
                .tradingDayCount(tradingDayCount)
                .latestPriceData(latestPriceData)
                .build();
    }

    private List<String> buildReasons(ScoreMetrics metrics, int dataReliabilityScore) {
        List<String> reasons = new ArrayList<>();

        if (gte(metrics.getReturn20d(), "5")) {
            reasons.add("20일 수익률이 양호합니다.");
        } else if (metrics.getReturn20d() != null && metrics.getReturn20d().compareTo(BigDecimal.ZERO) < 0) {
            reasons.add("20일 수익률이 음수입니다.");
        }

        if (gte(metrics.getHighProximity20d(), "95")) {
            reasons.add("현재가가 최근 20일 신고가에 가깝습니다.");
        }

        if (gte(metrics.getVolumeRatio5dTo20d(), "1.2")) {
            reasons.add("최근 5일 거래량이 20일 평균 대비 증가했습니다.");
        }

        if (metrics.getSharpDropCount20d() >= 3) {
            reasons.add("최근 20일 동안 급락 횟수가 많습니다.");
        }

        if (dataReliabilityScore < 8) {
            reasons.add("가격 데이터 최신성 또는 거래일 수가 충분하지 않습니다.");
        }

        reasons.add("포트폴리오 적합도는 임시로 10점 고정 반영했습니다.");
        return reasons;
    }

    private BigDecimal closePriceAt(List<StockPriceHistoryResult> prices, int daysAgo) {
        if (prices.size() <= daysAgo) {
            return null;
        }
        return toBigDecimal(prices.get(daysAgo).getClosePrice());
    }

    private BigDecimal returnRate(BigDecimal currentPrice, BigDecimal pastPrice) {
        if (currentPrice == null || pastPrice == null || pastPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        return currentPrice.subtract(pastPrice)
                .multiply(HUNDRED)
                .divide(pastPrice, 6, RoundingMode.HALF_UP);
    }

    private BigDecimal movingAverage(List<StockPriceHistoryResult> prices, int days) {
        if (prices.size() < days) {
            return null;
        }
        BigDecimal sum = prices.stream()
                .limit(days)
                .map(StockPriceHistoryResult::getClosePrice)
                .map(this::toBigDecimal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(days), 6, RoundingMode.HALF_UP);
    }

    private BigDecimal highProximity(List<StockPriceHistoryResult> prices, BigDecimal currentPrice, int days) {
        Long high = prices.stream()
                .limit(Math.min(days, prices.size()))
                .map(StockPriceHistoryResult::getHighPrice)
                .filter(value -> value != null && value > 0)
                .max(Long::compareTo)
                .orElse(null);
        if (currentPrice == null || high == null || high <= 0) {
            return null;
        }
        return currentPrice.multiply(HUNDRED)
                .divide(BigDecimal.valueOf(high), 6, RoundingMode.HALF_UP);
    }

    private BigDecimal volumeRatio(List<StockPriceHistoryResult> prices) {
        if (prices.size() < 20) {
            return null;
        }
        BigDecimal average5d = averageVolume(prices, 5);
        BigDecimal average20d = averageVolume(prices, 20);
        if (average5d == null || average20d == null || average20d.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        return average5d.divide(average20d, 6, RoundingMode.HALF_UP);
    }

    private BigDecimal averageVolume(List<StockPriceHistoryResult> prices, int days) {
        if (prices.size() < days) {
            return null;
        }
        BigDecimal sum = prices.stream()
                .limit(days)
                .map(StockPriceHistoryResult::getTradeQuantity)
                .map(this::toBigDecimal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(days), 6, RoundingMode.HALF_UP);
    }

    private BigDecimal volatility(List<StockPriceHistoryResult> prices, int days) {
        List<BigDecimal> rates = prices.stream()
                .limit(Math.min(days, prices.size()))
                .map(StockPriceHistoryResult::getFltRt)
                .filter(value -> value != null)
                .toList();
        if (rates.size() < 5) {
            return null;
        }

        BigDecimal mean = rates.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(rates.size()), 6, RoundingMode.HALF_UP);
        BigDecimal variance = rates.stream()
                .map(rate -> rate.subtract(mean).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(rates.size()), 6, RoundingMode.HALF_UP);

        return BigDecimal.valueOf(Math.sqrt(variance.doubleValue()));
    }

    private int sharpDropCount(List<StockPriceHistoryResult> prices, int days) {
        return (int) prices.stream()
                .limit(Math.min(days, prices.size()))
                .map(StockPriceHistoryResult::getFltRt)
                .filter(rate -> rate != null && rate.compareTo(BigDecimal.valueOf(-5)) <= 0)
                .count();
    }

    private BigDecimal drawdownFromHigh(List<StockPriceHistoryResult> prices, BigDecimal currentPrice, int days) {
        Long high = prices.stream()
                .limit(Math.min(days, prices.size()))
                .map(StockPriceHistoryResult::getHighPrice)
                .filter(value -> value != null && value > 0)
                .max(Long::compareTo)
                .orElse(null);
        if (currentPrice == null || high == null || high <= 0) {
            return null;
        }
        return BigDecimal.valueOf(high).subtract(currentPrice)
                .multiply(HUNDRED)
                .divide(BigDecimal.valueOf(high), 6, RoundingMode.HALF_UP);
    }

    private int scoreReturn(BigDecimal rate, int middleThreshold, int highThreshold, int maxScore) {
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        if (gte(rate, BigDecimal.valueOf(highThreshold))) {
            return maxScore;
        }
        if (gte(rate, BigDecimal.valueOf(middleThreshold))) {
            return Math.max(maxScore - 2, 0);
        }
        return Math.max(maxScore / 2, 1);
    }

    private boolean isLatestMarketData(StockPriceHistoryResult latest) {
        String latestMarketBasDt = stockPriceQueryPort.findLatestMarketBasDt();
        return latestMarketBasDt != null && latestMarketBasDt.equals(latest.getBasDt());
    }

    private boolean isAbove(BigDecimal value, BigDecimal target) {
        return value != null && target != null && value.compareTo(target) > 0;
    }

    private boolean gte(BigDecimal value, String target) {
        return gte(value, new BigDecimal(target));
    }

    private boolean gte(BigDecimal value, BigDecimal target) {
        return value != null && value.compareTo(target) >= 0;
    }

    private boolean lte(BigDecimal value, String target) {
        return value != null && value.compareTo(new BigDecimal(target)) <= 0;
    }

    private BigDecimal toBigDecimal(Long value) {
        return value == null ? BigDecimal.ZERO : BigDecimal.valueOf(value);
    }

    private BigDecimal scale(BigDecimal value) {
        return value == null ? null : value.setScale(2, RoundingMode.HALF_UP);
    }

    private String toOpinion(int totalScore) {
        if (totalScore >= 80) {
            return "STRONG";
        }
        if (totalScore >= 65) {
            return "POSITIVE";
        }
        if (totalScore >= 50) {
            return "WATCH";
        }
        if (totalScore >= 35) {
            return "CAUTION";
        }
        return "AVOID";
    }

    @lombok.Getter
    @lombok.Builder
    private static class ScoreMetrics {
        private BigDecimal currentPrice;
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
        private boolean latestPriceData;
    }
}
