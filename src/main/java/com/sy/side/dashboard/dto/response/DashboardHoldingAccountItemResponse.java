package com.sy.side.dashboard.dto.response;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardHoldingAccountItemResponse {
    private Long accountId;
    private String brokerName;
    private BigDecimal quantity;
    private BigDecimal averageBuyPrice;
    private BigDecimal buyAmount;
}
