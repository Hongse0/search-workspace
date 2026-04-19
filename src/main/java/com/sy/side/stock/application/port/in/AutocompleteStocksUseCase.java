package com.sy.side.stock.application.port.in;

import com.sy.side.stock.application.dto.command.StockAutocompleteCommand;
import com.sy.side.stock.application.dto.result.StockSearchResult;

public interface AutocompleteStocksUseCase {
    StockSearchResult autocomplete(StockAutocompleteCommand command);
}