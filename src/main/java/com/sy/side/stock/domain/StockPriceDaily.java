package com.sy.side.stock.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "stock_price_daily",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_stock_price_daily_srtn_cd_bas_dt", columnNames = {"srtn_cd", "bas_dt"})
        },
        indexes = {
                @Index(name = "idx_stock_price_daily_bas_dt", columnList = "bas_dt"),
                @Index(name = "idx_stock_price_daily_srtn_cd", columnList = "srtn_cd")
        }
)
public class StockPriceDaily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "srtn_cd", nullable = false, length = 16)
    private String srtnCd;

    @Column(name = "bas_dt", nullable = false, length = 8)
    private String basDt;

    @Column(name = "isin_cd", length = 16)
    private String isinCd;

    @Column(name = "itms_nm", length = 255)
    private String itmsNm;

    @Column(name = "mrkt_ctg", length = 16)
    private String mrktCtg;

    @Column(name = "clpr")
    private Long clpr;

    @Column(name = "vs")
    private Long vs;

    @Column(name = "flt_rt", precision = 10, scale = 2)
    private BigDecimal fltRt;

    @Column(name = "mkp")
    private Long mkp;

    @Column(name = "hipr")
    private Long hipr;

    @Column(name = "lopr")
    private Long lopr;

    @Column(name = "trqu")
    private Long trqu;

    @Column(name = "tr_prc")
    private Long trPrc;

    @Column(name = "lstg_st_cnt")
    private Long lstgStCnt;

    @Column(name = "mrkt_tot_amt")
    private Long mrktTotAmt;

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

    public static StockPriceDaily create(
            String srtnCd,
            String basDt,
            String isinCd,
            String itmsNm,
            String mrktCtg,
            Long clpr,
            Long vs,
            BigDecimal fltRt,
            Long mkp,
            Long hipr,
            Long lopr,
            Long trqu,
            Long trPrc,
            Long lstgStCnt,
            Long mrktTotAmt
    ) {
        StockPriceDaily entity = new StockPriceDaily();
        entity.srtnCd = srtnCd;
        entity.basDt = basDt;
        entity.isinCd = isinCd;
        entity.itmsNm = itmsNm;
        entity.mrktCtg = mrktCtg;
        entity.clpr = clpr;
        entity.vs = vs;
        entity.fltRt = fltRt;
        entity.mkp = mkp;
        entity.hipr = hipr;
        entity.lopr = lopr;
        entity.trqu = trqu;
        entity.trPrc = trPrc;
        entity.lstgStCnt = lstgStCnt;
        entity.mrktTotAmt = mrktTotAmt;
        return entity;
    }

    public void updateFrom(
            String isinCd,
            String itmsNm,
            String mrktCtg,
            Long clpr,
            Long vs,
            BigDecimal fltRt,
            Long mkp,
            Long hipr,
            Long lopr,
            Long trqu,
            Long trPrc,
            Long lstgStCnt,
            Long mrktTotAmt
    ) {
        this.isinCd = isinCd;
        this.itmsNm = itmsNm;
        this.mrktCtg = mrktCtg;
        this.clpr = clpr;
        this.vs = vs;
        this.fltRt = fltRt;
        this.mkp = mkp;
        this.hipr = hipr;
        this.lopr = lopr;
        this.trqu = trqu;
        this.trPrc = trPrc;
        this.lstgStCnt = lstgStCnt;
        this.mrktTotAmt = mrktTotAmt;
    }
}