package com.sy.side.account.dto.response;

import java.math.BigDecimal;

public record AccountCashResponse(
        Long accountId,
        BigDecimal cashBalance
) {
}