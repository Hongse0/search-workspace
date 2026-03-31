package com.sy.side.account.application.service;

import com.sy.side.account.application.port.in.CreateAccountUseCase;
import com.sy.side.account.application.port.in.DeleteAccountUseCase;
import com.sy.side.account.application.port.out.AccountCommandPort;
import com.sy.side.account.application.port.out.AccountQueryPort;
import com.sy.side.account.domain.Account;
import com.sy.side.account.dto.request.AccountCreateRequest;
import com.sy.side.account.dto.response.AccountResponse;
import com.sy.side.account.error.AccountErrorImpl;
import com.sy.side.common.exception.BizException;
import com.sy.side.trade.application.port.out.TradeCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountCommandService implements CreateAccountUseCase, DeleteAccountUseCase {

    private final AccountCommandPort accountCommandPort;
    private final AccountQueryPort accountQueryPort;
    private final TradeCommandPort tradeCommandPort;

    @Override
    public AccountResponse createAccount(Long memberId, AccountCreateRequest request) {
        if (accountQueryPort.existsByMemberIdAndBrokerNameAndAccountNumber(
                memberId,
                request.getBrokerName(),
                request.getAccountNumber()
        )) {
            throw new BizException(AccountErrorImpl.ACCOUNT_ERROR);
        }

        Account account = Account.create(
                memberId,
                request.getBrokerName(),
                request.getAccountNumber(),
                request.getAccountName(),
                request.getBaseCurrency(),
                request.getInitialBalance()
        );

        try {
            Account saved = accountCommandPort.save(account);
            return new AccountResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new BizException(AccountErrorImpl.ACCOUNT_ERROR);
        }
    }

    @Override
    public void deleteAccount(Long memberId, Long accountId) {
        Account account = accountQueryPort.findById(accountId)
                .orElseThrow(() -> new BizException(AccountErrorImpl.ACCOUNT_NOT_FOUND));

        account.validateOwner(memberId);

        tradeCommandPort.deleteByAccountId(accountId);
        accountCommandPort.delete(account);
    }
}