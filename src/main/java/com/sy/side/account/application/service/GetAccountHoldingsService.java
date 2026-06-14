package com.sy.side.account.application.service;

import com.sy.side.account.application.port.in.GetAccountHoldingsUseCase;
import com.sy.side.account.application.port.out.AccountQueryPort;
import com.sy.side.account.application.port.out.AccountStockPriceQueryPort;
import com.sy.side.account.error.AccountErrorImpl;
import com.sy.side.common.exception.BizException;
import com.sy.side.position.application.port.out.AccountPositionQueryPort;
import com.sy.side.trade.dto.AccountPositionSummary;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAccountHoldingsService implements GetAccountHoldingsUseCase {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final AccountQueryPort accountQueryPort;
    private final AccountPositionQueryPort accountPositionQueryPort;
    private final AccountStockPriceQueryPort accountStockPriceQueryPort;

    @Transactional(readOnly = true)
    @Override
    public List<AccountPositionSummary> getHoldings(Long memberId, Long accountId) {
        accountQueryPort.findByAccountIdAndMemberId(accountId, memberId)
                .orElseThrow(() -> new BizException(AccountErrorImpl.ACCOUNT_NOT_FOUND));

        List<AccountPositionSummary> holdings = accountPositionQueryPort.findAllByAccountId(accountId);
        enrichProfitRate(holdings);

        return holdings;
    }

    private void enrichProfitRate(List<AccountPositionSummary> holdings) {
        if (holdings == null || holdings.isEmpty()) {
            return;
        }

        Set<Long> stockIds = holdings.stream()
                .map(AccountPositionSummary::getStockId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, BigDecimal> currentPriceMap = accountStockPriceQueryPort.findLatestPriceMap(stockIds);

        holdings.forEach(holding -> {
            long quantity = holding.getQuantity() == null ? 0L : holding.getQuantity();
            BigDecimal avgPrice = nullSafe(holding.getAvgPrice());
            BigDecimal currentPrice = nullSafe(currentPriceMap.get(holding.getStockId()));
            BigDecimal buyAmount = avgPrice.multiply(BigDecimal.valueOf(quantity));
            BigDecimal evaluationAmount = currentPrice.multiply(BigDecimal.valueOf(quantity));
            BigDecimal profitLoss = evaluationAmount.subtract(buyAmount);
            BigDecimal profitRate = calculateRate(profitLoss, buyAmount);

            holding.setCurrentPrice(currentPrice);
            holding.setBuyAmount(buyAmount);
            holding.setEvaluationAmount(evaluationAmount);
            holding.setProfitLoss(profitLoss);
            holding.setProfitRate(profitRate);
            holding.setRate(profitRate);
        });
    }

    private BigDecimal calculateRate(BigDecimal numerator, BigDecimal denominator) {
        if (denominator == null || denominator.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return numerator.multiply(HUNDRED)
                .divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
