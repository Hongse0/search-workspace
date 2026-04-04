package com.sy.side.dashboard.application.port.out;

import com.sy.side.dashboard.dto.DashboardHoldingRow;
import java.util.List;

public interface DashboardHoldingQueryPort {
    List<DashboardHoldingRow> findHoldingsByMemberId(Long memberId);
}
