package com.sy.side.stock.controller;

import com.sy.side.stock.application.port.in.GetStockInvestmentScoreUseCase;
import com.sy.side.stock.application.port.in.SyncStockInvestmentScoreUseCase;
import com.sy.side.stock.dto.response.StockInvestmentScoreResponse;
import com.sy.side.stock.dto.response.StockInvestmentScoreSyncStartResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/stocks")
public class StockInvestmentScoreController {

    private final GetStockInvestmentScoreUseCase getStockInvestmentScoreUseCase;
    private final SyncStockInvestmentScoreUseCase syncStockInvestmentScoreUseCase;

    @GetMapping("/{srtnCd}/investment-score")
    public StockInvestmentScoreResponse getInvestmentScore(
            @PathVariable("srtnCd") @NotBlank String srtnCd
    ) {
        return getStockInvestmentScoreUseCase.getScore(srtnCd);
    }

    @PostMapping("/investment-scores/sync")
    public StockInvestmentScoreSyncStartResponse syncInvestmentScores() {
        String jobId = syncStockInvestmentScoreUseCase.startAsync();

        return StockInvestmentScoreSyncStartResponse.builder()
                .jobId(jobId)
                .status("STARTED")
                .message("종목 투자 의견 점수 동기화 배치가 시작되었습니다.")
                .build();
    }
}
