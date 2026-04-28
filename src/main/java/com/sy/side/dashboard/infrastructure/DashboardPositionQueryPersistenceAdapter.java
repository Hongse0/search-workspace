package com.sy.side.dashboard.infrastructure;

import com.sy.side.dashboard.application.port.out.DashboardPositionQueryPort;
import com.sy.side.position.application.port.out.AccountPositionQueryPort;
import com.sy.side.trade.dto.AccountPositionSummary;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DashboardPositionQueryPersistenceAdapter implements DashboardPositionQueryPort {

    private final AccountPositionQueryPort accountPositionQueryPort;

    @Override
    public List<AccountPositionSummary> findAllByAccountIds(List<Long> accountIds) {
        if (accountIds == null || accountIds.isEmpty()) {
            return List.of();
        }
        return accountPositionQueryPort.findAllByAccountIds(accountIds);
    }
}