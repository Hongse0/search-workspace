package com.sy.side.position.application.port.out;

import java.math.BigDecimal;

public interface PositionQueryPort {
    BigDecimal sumStockAssetValueByAccountId(Long accountId);
    Long countHoldingByAccountId(Long accountId);
}
