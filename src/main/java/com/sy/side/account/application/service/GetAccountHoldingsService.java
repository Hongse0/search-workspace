package com.sy.side.account.application.service;

import com.sy.side.account.application.port.in.GetAccountHoldingsUseCase;
import com.sy.side.account.application.port.out.AccountQueryPort;
import com.sy.side.account.error.AccountErrorImpl;
import com.sy.side.common.exception.BizException;
import com.sy.side.position.application.port.out.AccountPositionQueryPort;
import com.sy.side.trade.dto.AccountPositionSummary;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAccountHoldingsService implements GetAccountHoldingsUseCase {

    private final AccountQueryPort accountQueryPort;
    private final AccountPositionQueryPort accountPositionQueryPort;

    @Transactional(readOnly = true)
    @Override
    public List<AccountPositionSummary> getHoldings(Long memberId, Long accountId) {
        accountQueryPort.findByAccountIdAndMemberId(accountId, memberId)
                .orElseThrow(() -> new BizException(AccountErrorImpl.ACCOUNT_NOT_FOUND));

        return accountPositionQueryPort.findAllByAccountId(accountId);
    }
}