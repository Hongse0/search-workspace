package com.sy.side.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sy.side.stock.domain.StockItemMaster;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StockItemDocument {

    private Long id;

    @JsonProperty("bas_dt")
    private String basDt;

    @JsonProperty("srtn_cd")
    private String srtnCd;

    @JsonProperty("isin_cd")
    private String isinCd;

    @JsonProperty("mrkt_ctg")
    private String mrktCtg;

    @JsonProperty("itms_nm")
    private String itmsNm;

    @JsonProperty("crno")
    private String crno;

    @JsonProperty("corp_nm")
    private String corpNm;

    @JsonProperty("active_yn")
    private String activeYn;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public static StockItemDocument from(StockItemMaster e) {
        return StockItemDocument.builder()
                .id(e.getId())
                .basDt(e.getBasDt())
                .srtnCd(e.getSrtnCd())
                .isinCd(e.getIsinCd())
                .mrktCtg(e.getMrktCtg())
                .itmsNm(e.getItmsNm())
                .crno(e.getCrno())
                .corpNm(e.getCorpNm())
                .activeYn(e.getActiveYn())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
