package com.sy.side.account.application.service;

import com.sy.side.account.application.port.in.GetAccountUseCase;
import com.sy.side.account.application.port.out.AccountQueryPort;
import com.sy.side.account.domain.Account;
import com.sy.side.account.dto.response.AccountSelectResponse;
import com.sy.side.account.error.AccountErrorImpl;
import com.sy.side.common.exception.BizException;
import com.sy.side.position.application.port.out.PositionQueryPort;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountQueryService implements GetAccountUseCase {

    private final AccountQueryPort accountQueryPort;
    private final PositionQueryPort positionQueryPort;

    @Override
    public List<AccountSelectResponse> findAllAccount(Long memberId) {
        return accountQueryPort.findAllByMemberIdOrderByCreatedAtDesc(memberId)
                .stream()
                .map(this::toAccountSelectResponse)
                .toList();
    }

    @Override
    public AccountSelectResponse findMyAccount(Long memberId, Long accountId) {
        Account account = accountQueryPort.findByAccountIdAndMemberId(accountId, memberId)
                .orElseThrow(() -> new BizException(AccountErrorImpl.ACCOUNT_NOT_FOUND));

        return toAccountSelectResponse(account);
    }

    private AccountSelectResponse toAccountSelectResponse(Account account) {
        BigDecimal stockAssetValue = positionQueryPort.sumStockAssetValueByAccountId(account.getAccountId());
        Long holdingCount = positionQueryPort.countHoldingByAccountId(account.getAccountId());

        return new AccountSelectResponse(account, stockAssetValue, holdingCount);
    }
}