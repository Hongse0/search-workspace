package com.sy.side.stock.infrastructure;

import com.sy.side.stock.application.dto.result.StockInvestmentScoreSummaryResult;
import com.sy.side.stock.application.port.out.StockInvestmentScoreCommandPort;
import com.sy.side.stock.application.port.out.StockInvestmentScoreQueryPort;
import com.sy.side.stock.domain.StockInvestmentScoreDaily;
import com.sy.side.stock.dto.response.StockInvestmentScoreResponse;
import com.sy.side.stock.infrastructure.jpa.StockInvestmentScoreDailyRepository;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class StockInvestmentScorePersistenceAdapter
        implements StockInvestmentScoreCommandPort, StockInvestmentScoreQueryPort {

    private final StockInvestmentScoreDailyRepository stockInvestmentScoreDailyRepository;

    @Override
    @Transactional
    public void save(StockInvestmentScoreResponse score) {
        if (score.getBasDt() == null || score.getBasDt().isBlank()) {
            return;
        }

        StockInvestmentScoreDaily entity = stockInvestmentScoreDailyRepository
                .findBySrtnCdAndBasDt(score.getSrtnCd(), score.getBasDt())
                .map(existing -> {
                    existing.updateFrom(score);
                    return existing;
                })
                .orElseGet(() -> StockInvestmentScoreDaily.from(score));

        stockInvestmentScoreDailyRepository.save(entity);
    }

    @Override
    public Map<String, StockInvestmentScoreSummaryResult> findLatestScoreMapBySrtnCd(Set<String> srtnCds) {
        if (srtnCds == null || srtnCds.isEmpty()) {
            return Map.of();
        }

        return stockInvestmentScoreDailyRepository.findLatestScoresBySrtnCd(srtnCds).stream()
                .collect(Collectors.toMap(
                        StockInvestmentScoreDailyRepository.StockInvestmentScoreSummaryRow::getSrtnCd,
                        row -> StockInvestmentScoreSummaryResult.builder()
                                .srtnCd(row.getSrtnCd())
                                .basDt(row.getBasDt())
                                .totalScore(row.getTotalScore())
                                .opinion(row.getOpinion())
                                .build()
                ));
    }
}
