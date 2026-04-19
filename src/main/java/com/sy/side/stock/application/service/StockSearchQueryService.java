package com.sy.side.stock.application.service;

import com.sy.side.common.exception.BizException;
import com.sy.side.stock.application.dto.command.StockAutocompleteCommand;
import com.sy.side.stock.application.dto.command.StockSearchCommand;
import com.sy.side.stock.application.dto.result.StockSearchItemResult;
import com.sy.side.stock.application.dto.result.StockSearchResult;
import com.sy.side.stock.application.port.in.AutocompleteStocksUseCase;
import com.sy.side.stock.application.port.in.GetStockByCodeUseCase;
import com.sy.side.stock.application.port.in.SearchStocksUseCase;
import com.sy.side.stock.application.port.out.StockSearchPort;
import com.sy.side.stock.error.StockErrorImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockSearchQueryService
        implements SearchStocksUseCase, AutocompleteStocksUseCase, GetStockByCodeUseCase {

    private final StockSearchPort stockSearchPort;

    @Override
    public StockSearchResult search(StockSearchCommand command) {
        return stockSearchPort.search(command);
    }

    @Override
    public StockSearchResult autocomplete(StockAutocompleteCommand command) {
        return stockSearchPort.autocomplete(command);
    }

    @Override
    public StockSearchItemResult getBySrtnCd(String srtnCd) {
        return stockSearchPort.findBySrtnCd(srtnCd)
                .orElseThrow(() -> new BizException(StockErrorImpl.STOCK_NOT_FOUND));
    }
}