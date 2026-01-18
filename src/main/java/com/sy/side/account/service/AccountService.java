package com.sy.side.account.service;

import com.sy.side.account.dto.request.AccountCreateRequest;
import com.sy.side.account.dto.response.AccountResponse;
import com.sy.side.account.dto.response.AccountSelectResponse;
import com.sy.side.account.entity.Account;
import com.sy.side.account.error.AccountErrorImpl;
import com.sy.side.account.repository.AccountRepository;
import com.sy.side.common.error.ErrorCode;
import com.sy.side.common.error.ErrorCodeImpl;
import com.sy.side.common.exception.BizException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountResponse createAccount(Long memberId, AccountCreateRequest request) {

        if (accountRepository.existsByMemberIdAndBrokerNameAndAccountNumber(
                memberId,
                request.getBrokerName(),
                request.getAccountNumber()
        )) {
            throw new BizException(AccountErrorImpl.ACCOUNT_ERROR);
        }
        BigDecimal init = request.getInitialBalance();
        if (init == null) init = BigDecimal.ZERO;

        init = init.setScale(2, RoundingMode.DOWN);

        Account account = Account.builder()
                .memberId(memberId)
                .brokerName(request.getBrokerName())
                .accountNumber(request.getAccountNumber())
                .accountName(request.getAccountName())
                .baseCurrency(request.getBaseCurrency())
                .cashBalance(init)
                .build();

        try {
            Account saved = accountRepository.save(account);
            return new AccountResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new BizException(AccountErrorImpl.ACCOUNT_ERROR);
        }
    }

    /**
     * 내 계좌 전체 조회
     */
    @Transactional(readOnly = true)
    public List<AccountSelectResponse> findAllAccount(Long memberId) {
        return accountRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId)
                .stream()
                .map(AccountSelectResponse::new)
                .toList();
    }

    /**
     * 내 계좌 단건 조회
     */
    @Transactional(readOnly = true)
    public AccountSelectResponse findMyAccount(Long memberId, Long accountId) {
        Account account = accountRepository.findByAccountIdAndMemberId(accountId, memberId)
                .orElseThrow(() -> new BizException(AccountErrorImpl.ACCOUNT_NOT_FOUND));

        return new AccountSelectResponse(account);
    }

    @Transactional
    public void deleteAccount(Long memberId, Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BizException(AccountErrorImpl.ACCOUNT_NOT_FOUND));

        if (!account.getMemberId().equals(memberId)) {
            throw new BizException(AccountErrorImpl.ACCOUNT_FORBIDDEN);
        }

        accountRepository.delete(account);
    }
}

