package com.sy.side.stock.application.service;

import com.sy.side.common.exception.BizException;
import com.sy.side.stock.application.dto.command.StockAutocompleteCommand;
import com.sy.side.stock.application.dto.command.StockSearchCommand;
import com.sy.side.stock.application.dto.result.StockSearchItemResult;
import com.sy.side.stock.application.dto.result.StockSearchResult;
import com.sy.side.stock.application.port.in.AutocompleteStocksUseCase;
import com.sy.side.stock.application.port.in.GetStockByCodeUseCase;
import com.sy.side.stock.application.port.in.SearchStocksUseCase;
import com.sy.side.stock.application.port.out.StockPriceQueryPort;
import com.sy.side.stock.application.port.out.StockSearchPort;
import com.sy.side.stock.error.StockErrorImpl;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockSearchQueryService
        implements SearchStocksUseCase, AutocompleteStocksUseCase, GetStockByCodeUseCase {

    private final StockSearchPort stockSearchPort;
    private final StockPriceQueryPort stockPriceQueryPort;

    @Override
    public StockSearchResult search(StockSearchCommand command) {
        return withCurrentPrices(stockSearchPort.search(command));
    }

    @Override
    public StockSearchResult autocomplete(StockAutocompleteCommand command) {
        return withCurrentPrices(stockSearchPort.autocomplete(command));
    }

    @Override
    public StockSearchItemResult getBySrtnCd(String srtnCd) {
        StockSearchItemResult result = stockSearchPort.findBySrtnCd(srtnCd)
                .orElseThrow(() -> new BizException(StockErrorImpl.STOCK_NOT_FOUND));

        Map<String, BigDecimal> priceMap = result.getSrtnCd() == null
                ? Map.of()
                : stockPriceQueryPort.findLatestPriceMapBySrtnCd(Set.of(result.getSrtnCd()));

        return withCurrentPrice(result, priceMap);
    }

    private StockSearchResult withCurrentPrices(StockSearchResult result) {
        Set<String> srtnCds = result.getItems().stream()
                .map(StockSearchItemResult::getSrtnCd)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<String, BigDecimal> priceMap = stockPriceQueryPort.findLatestPriceMapBySrtnCd(srtnCds);

        return StockSearchResult.builder()
                .query(result.getQuery())
                .size(result.getSize())
                .total(result.getTotal())
                .items(result.getItems().stream()
                        .map(item -> withCurrentPrice(item, priceMap))
                        .toList())
                .build();
    }

    private StockSearchItemResult withCurrentPrice(
            StockSearchItemResult item,
            Map<String, BigDecimal> priceMap
    ) {
        return StockSearchItemResult.builder()
                .srtnCd(item.getSrtnCd())
                .isinCd(item.getIsinCd())
                .mrktCtg(item.getMrktCtg())
                .itmsNm(item.getItmsNm())
                .corpNm(item.getCorpNm())
                .activeYn(item.getActiveYn())
                .basDt(item.getBasDt())
                .currentPrice(priceMap.get(item.getSrtnCd()))
                .build();
    }
}
