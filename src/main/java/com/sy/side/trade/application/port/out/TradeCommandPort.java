package com.sy.side.trade.application.port.out;

import com.sy.side.stock.application.dto.command.TradeInsertCommand;

public interface TradeCommandPort {
    Long insertTrade(TradeInsertCommand cmd);
    void deleteByAccountId(Long accountId);
}