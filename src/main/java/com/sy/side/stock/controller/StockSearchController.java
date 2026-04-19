package com.sy.side.stock.controller;

import com.sy.side.stock.application.dto.command.StockAutocompleteCommand;
import com.sy.side.stock.application.dto.command.StockSearchCommand;
import com.sy.side.stock.application.port.in.AutocompleteStocksUseCase;
import com.sy.side.stock.application.port.in.GetStockByCodeUseCase;
import com.sy.side.stock.application.port.in.SearchStocksUseCase;
import com.sy.side.stock.dto.request.StockSearchRequest;
import com.sy.side.stock.dto.response.StockSearchItemResponse;
import com.sy.side.stock.dto.response.StockSearchResponse;
import com.sy.side.stock.infrastructure.mapper.StockSearchWebMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/stocks/search")
public class StockSearchController {

    private final SearchStocksUseCase searchStocksUseCase;
    private final AutocompleteStocksUseCase autocompleteStocksUseCase;
    private final GetStockByCodeUseCase getStockByCodeUseCase;
    private final StockSearchWebMapper stockSearchWebMapper;

    @PostMapping
    public StockSearchResponse search(@Valid @RequestBody StockSearchRequest request) {
        StockSearchCommand command = stockSearchWebMapper.toSearchCommand(request);
        return stockSearchWebMapper.toResponse(searchStocksUseCase.search(command));
    }

    @GetMapping("/autocomplete")
    public StockSearchResponse autocomplete(
            @RequestParam("q") @NotBlank String q,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(50) int size
    ) {
        StockAutocompleteCommand command = StockAutocompleteCommand.builder()
                .q(q)
                .size(size)
                .build();

        return stockSearchWebMapper.toResponse(
                autocompleteStocksUseCase.autocomplete(command)
        );
    }

    @GetMapping("/{srtnCd}")
    public StockSearchItemResponse getBySrtnCd(
            @PathVariable("srtnCd") @NotBlank String srtnCd
    ) {
        return stockSearchWebMapper.toItemResponse(
                getStockByCodeUseCase.getBySrtnCd(srtnCd)
        );
    }
}