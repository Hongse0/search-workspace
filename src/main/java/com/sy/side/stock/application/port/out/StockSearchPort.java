package com.sy.side.stock.application.port.out;

import com.sy.side.stock.application.dto.command.StockAutocompleteCommand;
import com.sy.side.stock.application.dto.command.StockSearchCommand;
import com.sy.side.stock.application.dto.result.StockSearchItemResult;
import com.sy.side.stock.application.dto.result.StockSearchResult;

import java.util.Optional;

public interface StockSearchPort {

    StockSearchResult search(StockSearchCommand command);

    StockSearchResult autocomplete(StockAutocompleteCommand command);

    Optional<StockSearchItemResult> findBySrtnCd(String srtnCd);
}