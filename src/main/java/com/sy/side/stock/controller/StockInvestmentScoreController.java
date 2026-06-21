package com.sy.side.stock.controller;

import com.sy.side.stock.application.port.in.GetStockInvestmentScoreUseCase;
import com.sy.side.stock.dto.response.StockInvestmentScoreResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/stocks")
public class StockInvestmentScoreController {

    private final GetStockInvestmentScoreUseCase getStockInvestmentScoreUseCase;

    @GetMapping("/{srtnCd}/investment-score")
    public StockInvestmentScoreResponse getInvestmentScore(
            @PathVariable("srtnCd") @NotBlank String srtnCd
    ) {
        return getStockInvestmentScoreUseCase.getScore(srtnCd);
    }
}
