package com.sy.side.stock.application.port.in;

import com.sy.side.stock.application.dto.result.StockSearchItemResult;

public interface GetStockByCodeUseCase {
    StockSearchItemResult getBySrtnCd(String srtnCd);
}