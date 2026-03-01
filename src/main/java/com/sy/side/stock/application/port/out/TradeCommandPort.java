package com.sy.side.stock.application.port.out;

import com.sy.side.stock.application.dto.command.TradeInsertCommand;
import com.sy.side.stock.dto.request.BuyStockRequest;

public interface TradeCommandPort {
    Long insertTrade(TradeInsertCommand cmd);
}