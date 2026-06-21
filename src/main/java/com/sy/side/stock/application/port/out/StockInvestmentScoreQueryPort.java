package com.sy.side.stock.application.port.out;

import com.sy.side.stock.application.dto.result.StockInvestmentScoreSummaryResult;
import java.util.Map;
import java.util.Set;

public interface StockInvestmentScoreQueryPort {
    Map<String, StockInvestmentScoreSummaryResult> findLatestScoreMapBySrtnCd(Set<String> srtnCds);
}
