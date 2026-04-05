package com.sy.side.stock.application.port.out;

import com.sy.side.stock.application.dto.command.ApplyBuyCommand;

public interface AccountPositionCommandPort {
    void applyBuy(ApplyBuyCommand applyBuyCommand);
    void deleteAllByAccountId(Long accountId);
}
