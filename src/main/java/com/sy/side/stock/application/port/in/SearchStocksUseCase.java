package com.sy.side.stock.application.port.in;

import com.sy.side.stock.application.dto.command.StockSearchCommand;
import com.sy.side.stock.application.dto.result.StockSearchResult;

public interface SearchStocksUseCase {
    StockSearchResult search(StockSearchCommand command);
}