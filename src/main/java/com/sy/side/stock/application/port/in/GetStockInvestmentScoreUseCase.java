package com.sy.side.stock.application.port.in;

import com.sy.side.stock.dto.response.StockInvestmentScoreResponse;

public interface GetStockInvestmentScoreUseCase {
    StockInvestmentScoreResponse getScore(String srtnCd);
}
