package com.sy.side.stock.domain;

import com.sy.side.stock.dto.response.StockInvestmentScoreResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "stock_investment_score_daily",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_stock_investment_score_srtn_cd_bas_dt", columnNames = {"srtn_cd", "bas_dt"})
        },
        indexes = {
                @Index(name = "idx_stock_investment_score_bas_dt", columnList = "bas_dt"),
                @Index(name = "idx_stock_investment_score_srtn_cd", columnList = "srtn_cd"),
                @Index(name = "idx_stock_investment_score_total", columnList = "total_score")
        }
)
public class StockInvestmentScoreDaily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "srtn_cd", nullable = false, length = 16)
    private String srtnCd;

    @Column(name = "itms_nm", length = 255)
    private String itmsNm;

    @Column(name = "bas_dt", nullable = false, length = 8)
    private String basDt;

    @Column(name = "total_score", nullable = false)
    private Integer totalScore;

    @Column(name = "trend_score", nullable = false)
    private Integer trendScore;

    @Column(name = "momentum_score", nullable = false)
    private Integer momentumScore;

    @Column(name = "volatility_risk_score", nullable = false)
    private Integer volatilityRiskScore;

    @Column(name = "data_reliability_score", nullable = false)
    private Integer dataReliabilityScore;

    @Column(name = "portfolio_fit_score", nullable = false)
    private Integer portfolioFitScore;

    @Column(name = "opinion", nullable = false, length = 20)
    private String opinion;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static StockInvestmentScoreDaily from(StockInvestmentScoreResponse score) {
        StockInvestmentScoreDaily entity = new StockInvestmentScoreDaily();
        entity.srtnCd = score.getSrtnCd();
        entity.basDt = score.getBasDt();
        entity.updateFrom(score);
        return entity;
    }

    public void updateFrom(StockInvestmentScoreResponse score) {
        this.itmsNm = score.getItmsNm();
        this.totalScore = score.getTotalScore();
        this.trendScore = score.getTrendScore();
        this.momentumScore = score.getMomentumScore();
        this.volatilityRiskScore = score.getVolatilityRiskScore();
        this.dataReliabilityScore = score.getDataReliabilityScore();
        this.portfolioFitScore = score.getPortfolioFitScore();
        this.opinion = score.getOpinion();
    }
}
