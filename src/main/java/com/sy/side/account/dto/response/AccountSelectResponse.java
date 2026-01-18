package com.sy.side.account.dto.response;

import com.sy.side.account.entity.Account;
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
    private final LocalDateTime createdAt;

    public AccountSelectResponse(Account account) {
        this.accountId = account.getAccountId();
        this.brokerName = account.getBrokerName();
        this.accountNumber = account.getAccountNumber();
        this.accountName = account.getAccountName();
        this.baseCurrency = account.getBaseCurrency();
        this.cashBalance = account.getCashBalance();
        this.createdAt = account.getCreatedAt();
    }
}
