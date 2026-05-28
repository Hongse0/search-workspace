package com.sy.side.account.dto.response;

import com.sy.side.account.domain.Account;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountResponse {

    private Long accountId;
    private Long memberId;
    private String brokerName;
    private String accountNumber;
    private String accountName;
    private String baseCurrency;
    private String cashBalance;
    private String stockAssetValue;
    private String totalAssetValue;
    private Long holdingCount;
    private LocalDateTime createdAt;

    public AccountResponse(Account account) {
        this.accountId = account.getAccountId();
        this.memberId = account.getMemberId();
        this.brokerName = account.getBrokerName();
        this.accountNumber = account.getAccountNumber();
        this.accountName = account.getAccountName();
        this.baseCurrency = account.getBaseCurrency();
        this.cashBalance = account.getCashBalance().toPlainString();
        this.stockAssetValue = account.getStockAssetValue().toPlainString();
        this.totalAssetValue = account.getTotalAssetValue().toPlainString();
        this.holdingCount = account.getHoldingCount();
        this.createdAt = account.getCreatedAt();
    }
}
