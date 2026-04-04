package com.sy.side.dashboard.application.port.in;

import com.sy.side.dashboard.dto.response.DashboardHoldingsResponse;

public interface GetDashboardHoldingsUseCase {
    DashboardHoldingsResponse getHoldings(Long memberId);
}
