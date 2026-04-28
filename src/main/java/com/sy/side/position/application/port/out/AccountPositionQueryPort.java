package com.sy.side.position.application.port.out;

import com.sy.side.trade.dto.AccountPositionSummary;
import java.util.List;
import java.util.Optional;

public interface AccountPositionQueryPort {
    Optional<AccountPositionSummary> findByAccountIdAndStockId(Long accountId, Long stockId);
    List<AccountPositionSummary> findAllByAccountId(Long accountId);

    List<AccountPositionSummary> findAllByAccountIds(List<Long> accountIds);
}