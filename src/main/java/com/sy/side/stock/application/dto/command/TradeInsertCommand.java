package com.sy.side.stock.application.dto.command;

import com.sy.side.trade.domain.Market;
import com.sy.side.trade.domain.TradeSide;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TradeInsertCommand(
        Long accountId,
        Long stockId,
        Market market,
        TradeSide side,
        Long quantity,
        BigDecimal price,
        BigDecimal fee,
        BigDecimal tax,
        BigDecimal totalAmount,
        LocalDateTime tradeDateTime,
        String memo
) {}