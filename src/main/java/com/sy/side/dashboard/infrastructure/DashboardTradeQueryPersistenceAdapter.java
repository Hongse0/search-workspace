package com.sy.side.dashboard.infrastructure;

import com.sy.side.dashboard.application.port.out.DashboardTradeQueryPort;
import com.sy.side.trade.dto.RecentTradeSummary;
import com.sy.side.trade.infrastructure.jpa.TradeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DashboardTradeQueryPersistenceAdapter implements DashboardTradeQueryPort {

    private final TradeRepository tradeRepository;

    @Override
    public List<RecentTradeSummary> findRecentByAccountIds(List<Long> accountIds, int limit) {
        if (accountIds == null || accountIds.isEmpty()) {
            return List.of();
        }

        return tradeRepository.findRecentTradeSummariesByAccountIds(
                accountIds,
                org.springframework.data.domain.PageRequest.of(0, limit)
        );
    }
}