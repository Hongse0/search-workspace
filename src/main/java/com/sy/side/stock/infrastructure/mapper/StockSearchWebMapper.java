package com.sy.side.stock.infrastructure.mapper;

import com.sy.side.stock.application.dto.command.StockSearchCommand;
import com.sy.side.stock.application.dto.result.StockSearchItemResult;
import com.sy.side.stock.application.dto.result.StockSearchResult;
import com.sy.side.stock.dto.request.StockSearchRequest;
import com.sy.side.stock.dto.response.StockSearchItemResponse;
import com.sy.side.stock.dto.response.StockSearchResponse;
import com.sy.side.stock.dto.response.StockInvestmentScoreSummaryResponse;
import org.springframework.stereotype.Component;

@Component
public class StockSearchWebMapper {

    public StockSearchCommand toSearchCommand(StockSearchRequest request) {
        return StockSearchCommand.builder()
                .q(request.getQ())
                .mrktCtg(request.getMrktCtg())
                .activeYn(request.getActiveYn())
                .basDt(request.getBasDt())
                .size(request.getSize())
                .fuzzy(request.isFuzzy())
                .build();
    }

    public StockSearchResponse toResponse(StockSearchResult result) {
        return StockSearchResponse.builder()
                .query(result.getQuery())
                .size(result.getSize())
                .total(result.getTotal())
                .items(result.getItems().stream()
                        .map(this::toItemResponse)
                        .toList())
                .build();
    }

    public StockSearchItemResponse toItemResponse(StockSearchItemResult result) {
        return StockSearchItemResponse.builder()
                .srtnCd(result.getSrtnCd())
                .isinCd(result.getIsinCd())
                .mrktCtg(result.getMrktCtg())
                .itmsNm(result.getItmsNm())
                .corpNm(result.getCorpNm())
                .activeYn(result.getActiveYn())
                .basDt(result.getBasDt())
                .currentPrice(result.getCurrentPrice())
                .vs(result.getVs())
                .fltRt(result.getFltRt())
                .investmentScore(toInvestmentScoreResponse(result))
                .build();
    }

    private StockInvestmentScoreSummaryResponse toInvestmentScoreResponse(StockSearchItemResult result) {
        if (result.getInvestmentScore() == null) {
            return null;
        }

        return StockInvestmentScoreSummaryResponse.builder()
                .basDt(result.getInvestmentScore().getBasDt())
                .totalScore(result.getInvestmentScore().getTotalScore())
                .opinion(result.getInvestmentScore().getOpinion())
                .build();
    }
}
