package com.sy.side.account.service;

import com.sy.side.account.dto.request.AccountCreateRequest;
import com.sy.side.account.dto.response.AccountResponse;
import com.sy.side.account.entity.Account;
import com.sy.side.account.error.AccountErrorImpl;
import com.sy.side.account.repository.AccountRepository;
import com.sy.side.common.error.ErrorCode;
import com.sy.side.common.error.ErrorCodeImpl;
import com.sy.side.common.exception.BizException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountResponse createAccount(Long memberId, AccountCreateRequest request) {

        if (accountRepository.existsByMemberIdAndAccountNumber(memberId, request.getAccountNumber())) {
            throw new BizException(AccountErrorImpl.ACCOUNT_ERROR);
        }

        Account account = Account.builder()
                .memberId(memberId)
                .brokerName(request.getBrokerName())
                .accountNumber(request.getAccountNumber())
                .accountName(request.getAccountName())
                .baseCurrency(request.getBaseCurrency())
                .build();

        try {
            accountRepository.save(account);
        } catch (DataIntegrityViolationException e) {
            throw new BizException(AccountErrorImpl.ACCOUNT_ERROR);
        }


        return new AccountResponse(account);
    }

}

