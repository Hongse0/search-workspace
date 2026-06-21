package com.sy.side.stock.application.port.out;

import com.sy.side.stock.dto.response.StockInvestmentScoreResponse;

public interface StockInvestmentScoreCommandPort {
    void save(StockInvestmentScoreResponse score);
}
