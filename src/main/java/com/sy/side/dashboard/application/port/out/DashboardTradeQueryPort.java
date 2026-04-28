package com.sy.side.dashboard.application.port.out;

import com.sy.side.trade.dto.RecentTradeSummary;
import java.util.List;

public interface DashboardTradeQueryPort {
    List<RecentTradeSummary> findRecentByAccountIds(List<Long> accountIds, int recentTradeLimit);
}