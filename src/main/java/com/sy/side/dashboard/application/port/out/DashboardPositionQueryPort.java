package com.sy.side.dashboard.application.port.out;

import com.sy.side.trade.dto.AccountPositionSummary;
import java.util.List;

public interface DashboardPositionQueryPort {
    List<AccountPositionSummary> findAllByAccountIds(List<Long> accountIds);
}