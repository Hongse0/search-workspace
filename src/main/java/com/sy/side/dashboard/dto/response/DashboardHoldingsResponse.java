package com.sy.side.dashboard.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardHoldingsResponse {
    private DashboardSummaryResponse summary;
    private List<DashboardHoldingItemResponse> holdings;
}
