package com.sy.side.stock.application.port.out;

import com.sy.side.stock.application.dto.command.ApplyBuyCommand;
import com.sy.side.trade.dto.ApplySellCommand;

public interface AccountPositionCommandPort {
    void applyBuy(ApplyBuyCommand applyBuyCommand);
    void deleteAllByAccountId(Long accountId);
    void applySell(ApplySellCommand applySellCommand);
}
