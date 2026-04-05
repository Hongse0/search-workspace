package com.sy.side.account.dto.response;

import com.sy.side.account.domain.Account;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class AccountSelectResponse {

    private final Long accountId;
    private final String brokerName;
    private final String accountNumber;
    private final String accountName;
    private final String baseCurrency;
    private final BigDecimal cashBalance;
    private final BigDecimal stockAssetValue;
    private final BigDecimal totalAssetValue;
    private final Long holdingCount;
    private final LocalDateTime createdAt;

    public AccountSelectResponse(Account account, BigDecimal stockAssetValue, Long holdingCount) {
        BigDecimal safeCashBalance = account.getCashBalance() != null ? account.getCashBalance() : BigDecimal.ZERO;
        BigDecimal safeStockAssetValue = stockAssetValue != null ? stockAssetValue : BigDecimal.ZERO;
        Long safeHoldingCount = holdingCount != null ? holdingCount : 0L;

        this.accountId = account.getAccountId();
        this.brokerName = account.getBrokerName();
        this.accountNumber = account.getAccountNumber();
        this.accountName = account.getAccountName();
        this.baseCurrency = account.getBaseCurrency();
        this.cashBalance = safeCashBalance;
        this.stockAssetValue = safeStockAssetValue;
        this.totalAssetValue = safeCashBalance.add(safeStockAssetValue);
        this.holdingCount = safeHoldingCount;
        this.createdAt = account.getCreatedAt();
    }
}