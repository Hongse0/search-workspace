package com.sy.side.account.application.port.in;

import com.sy.side.account.dto.request.AccountCashRequest;
import com.sy.side.account.dto.response.AccountCashResponse;

public interface WithdrawAccountCashUseCase {
    AccountCashResponse withdraw(Long memberId, Long accountId, AccountCashRequest request);
}