package com.sy.side.account.application.port.in;

import com.sy.side.trade.dto.AccountPositionSummary;
import java.util.List;

public interface GetAccountHoldingsUseCase {
    List<AccountPositionSummary> getHoldings(Long memberId, Long accountId);
}