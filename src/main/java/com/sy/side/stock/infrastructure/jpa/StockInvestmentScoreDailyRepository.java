package com.sy.side.stock.infrastructure.jpa;

import com.sy.side.stock.domain.StockInvestmentScoreDaily;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockInvestmentScoreDailyRepository extends JpaRepository<StockInvestmentScoreDaily, Long> {
    Optional<StockInvestmentScoreDaily> findBySrtnCdAndBasDt(String srtnCd, String basDt);

    @Query(value = """
    SELECT
        sis.srtn_cd AS srtnCd,
        sis.bas_dt AS basDt,
        sis.total_score AS totalScore,
        sis.opinion AS opinion
    FROM stock_investment_score_daily sis
    WHERE sis.srtn_cd IN (:srtnCds)
    AND sis.bas_dt = (
        SELECT MAX(sis2.bas_dt)
        FROM stock_investment_score_daily sis2
        WHERE sis2.srtn_cd = sis.srtn_cd
    )
""", nativeQuery = true)
    List<StockInvestmentScoreSummaryRow> findLatestScoresBySrtnCd(@Param("srtnCds") Set<String> srtnCds);

    interface StockInvestmentScoreSummaryRow {
        String getSrtnCd();
        String getBasDt();
        Integer getTotalScore();
        String getOpinion();
    }
}
