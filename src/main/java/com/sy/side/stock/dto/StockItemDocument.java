package com.sy.side.stock.dto;

import com.sy.side.stock.domain.StockItemMaster;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Getter
@Builder
public class StockItemDocument {

    @Field(type = FieldType.Long, name = "id")
    private Long id;

    @Field(type = FieldType.Keyword, name = "bas_dt")
    private String basDt;

    @Field(type = FieldType.Keyword, name = "srtn_cd")
    private String srtnCd;

    @Field(type = FieldType.Keyword, name = "isin_cd")
    private String isinCd;

    @Field(type = FieldType.Keyword, name = "mrkt_ctg")
    private String mrktCtg;

    @Field(type = FieldType.Text, name = "itms_nm")
    private String itmsNm;

    @Field(type = FieldType.Keyword, name = "crno")
    private String crno;

    @Field(type = FieldType.Text, name = "corp_nm")
    private String corpNm;

    @Field(type = FieldType.Keyword, name = "active_yn")
    private String activeYn;

    @Field(type = FieldType.Date, name = "created_at")
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date, name = "updated_at")
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