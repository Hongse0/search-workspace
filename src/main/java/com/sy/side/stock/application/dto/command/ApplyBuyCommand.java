package com.sy.side.stock.application.dto.command;

import com.sy.side.trade.domain.Market;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApplyBuyCommand {
    private final Long accountId;
    private final Long stockId;
    private final Market market;
    private final Long quantity;
    private final BigDecimal price;
    private final BigDecimal fee;
    private final BigDecimal tax;
    private final Long tradeId;
    private final LocalDateTime tradeDateTime;
}