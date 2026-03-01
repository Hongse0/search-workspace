package com.sy.side.stock.controller;

import com.sy.side.stock.dto.request.StockSearchRequest;
import com.sy.side.stock.dto.response.StockSearchResponse;
import com.sy.side.stock.service.StockSearchService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/stocks/search")
public class StockSearchController {

    private final StockSearchService stockSearchService;

    @PostMapping
    public StockSearchResponse search(@Valid @RequestBody StockSearchRequest req) {
        return stockSearchService.search(req);
    }

    /**
     * 자동완성
     * GET /v1/stocks/search/autocomplete?q=삼성&size=10
     */
    @GetMapping("/autocomplete")
    public StockSearchResponse autocomplete(
            @RequestParam("q") @NotBlank String q,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(50) int size
    ) {
        return stockSearchService.autocomplete(q, size);
    }

    /**
     * 단축코드 단건조회
     * GET /v1/stocks/search/A005930
     */
    @GetMapping("/{srtnCd}")
    public StockSearchResponse.SearchItem getBySrtnCd(
            @PathVariable("srtnCd") @NotBlank String srtnCd
    ) {
        return stockSearchService.getBySrtnCd(srtnCd);
    }
}
