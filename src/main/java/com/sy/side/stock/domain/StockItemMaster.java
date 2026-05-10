package com.sy.side.stock.domain;

import com.sy.side.search.api.dto.response.KrxListedInfoItem;
import com.sy.side.stock.dto.response.EtfPriceInfoItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
        name = "stock_item_master",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_srtn_cd", columnNames = "srtn_cd")
        },
        indexes = {
                @Index(name = "idx_itms_nm", columnList = "itms_nm"),
                @Index(name = "idx_corp_nm", columnList = "corp_nm"),
                @Index(name = "idx_isin_cd", columnList = "isin_cd"),
                @Index(name = "idx_mrkt_ctg", columnList = "mrkt_ctg"),
                @Index(name = "idx_item_type", columnList = "item_type")
        }
)
public class StockItemMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** KRX 기준일자 (YYYYMMDD) */
    @Column(name = "bas_dt", length = 8)
    private String basDt;

    /** KRX 단축 종목 코드 */
    @Column(name = "srtn_cd", length = 16, nullable = false)
    private String srtnCd;

    /** ISIN 코드 */
    @Column(name = "isin_cd", length = 16)
    private String isinCd;

    /** 시장 구분 (KOSPI / KOSDAQ / KONEX / ETF) */
    @Column(name = "mrkt_ctg", length = 16)
    private String mrktCtg;

    /** 종목 유형 (STOCK / ETF / ETN / ELW) */
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", length = 20, nullable = false)
    private StockItemType itemType;

    /** 종목명 */
    @Column(name = "itms_nm", length = 255)
    private String itmsNm;

    /** 법인등록번호 */
    @Column(name = "crno", length = 32)
    private String crno;

    /** 법인명 */
    @Column(name = "corp_nm", length = 255)
    private String corpNm;

    /** 활성 여부 */
    @Column(name = "active_yn", length = 1)
    private String activeYn;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.activeYn = this.activeYn == null ? "Y" : this.activeYn;
        this.itemType = this.itemType == null ? StockItemType.STOCK : this.itemType;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static StockItemMaster from(KrxListedInfoItem item) {
        StockItemMaster e = new StockItemMaster();
        e.basDt = item.basDt();
        e.srtnCd = item.srtnCd();
        e.isinCd = item.isinCd();
        e.mrktCtg = item.mrktCtg();
        e.itemType = StockItemType.STOCK;
        e.itmsNm = item.itmsNm();
        e.crno = item.crno();
        e.corpNm = item.corpNm();
        e.activeYn = "Y";
        return e;
    }

    public void updateFrom(KrxListedInfoItem item) {
        this.basDt = item.basDt();
        this.isinCd = item.isinCd();
        this.mrktCtg = item.mrktCtg();
        this.itemType = StockItemType.STOCK;
        this.itmsNm = item.itmsNm();
        this.crno = item.crno();
        this.corpNm = item.corpNm();
        this.activeYn = "Y";
    }

    public static StockItemMaster fromEtf(EtfPriceInfoItem item) {
        StockItemMaster e = new StockItemMaster();
        e.basDt = item.getBasDt();
        e.srtnCd = item.getSrtnCd();
        e.isinCd = item.getIsinCd();
        e.mrktCtg = "ETF";
        e.itemType = StockItemType.ETF;
        e.itmsNm = item.getItmsNm();
        e.crno = null;
        e.corpNm = item.getItmsNm();
        e.activeYn = "Y";
        return e;
    }

    public void updateFromEtf(EtfPriceInfoItem item) {
        this.basDt = item.getBasDt();
        this.isinCd = item.getIsinCd();
        this.mrktCtg = "ETF";
        this.itemType = StockItemType.ETF;
        this.itmsNm = item.getItmsNm();
        this.crno = null;
        this.corpNm = item.getItmsNm();
        this.activeYn = "Y";
    }

    public String tradeSymbol() {
        return this.srtnCd;
    }

    public boolean isEtf() {
        return this.itemType == StockItemType.ETF;
    }

    public boolean isStock() {
        return this.itemType == StockItemType.STOCK;
    }
}