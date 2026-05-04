package com.sy.side.account.application.service;

import com.sy.side.account.application.port.in.DepositAccountCashUseCase;
import com.sy.side.account.application.port.in.WithdrawAccountCashUseCase;
import com.sy.side.account.application.port.out.AccountCommandPort;
import com.sy.side.account.domain.Account;
import com.sy.side.account.dto.request.AccountCashRequest;
import com.sy.side.account.dto.response.AccountCashResponse;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountCashService implements DepositAccountCashUseCase, WithdrawAccountCashUseCase {

    private final AccountCommandPort accountCommandPort;

    @Override
    @Transactional
    public AccountCashResponse deposit(Long memberId, Long accountId, AccountCashRequest request) {
        Account account = accountCommandPort.getActiveAccountForUpdate(memberId, accountId);

        account.validateOwner(memberId);
        account.validateActive();

        account.deposit(request.amount().setScale(2, RoundingMode.DOWN));

        Account savedAccount = accountCommandPort.save(account);

        return new AccountCashResponse(
                savedAccount.getAccountId(),
                savedAccount.getCashBalance()
        );
    }

    @Override
    @Transactional
    public AccountCashResponse withdraw(Long memberId, Long accountId, AccountCashRequest request) {
        Account account = accountCommandPort.getActiveAccountForUpdate(memberId, accountId);

        account.validateOwner(memberId);
        account.validateActive();

        account.withdraw(request.amount().setScale(2, RoundingMode.DOWN));

        Account savedAccount = accountCommandPort.save(account);

        return new AccountCashResponse(
                savedAccount.getAccountId(),
                savedAccount.getCashBalance()
        );
    }
}