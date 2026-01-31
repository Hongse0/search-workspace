package com.sy.side.stock.dto;

import com.sy.side.stock.domain.StockItemMaster;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockItemDocument {

    private Long id;
    private String basDt;
    private String srtnCd;
    private String isinCd;
    private String mrktCtg;
    private String itmsNm;
    private String crno;
    private String corpNm;
    private String activeYn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static StockItemDocument from(
            StockItemMaster e
    ) {
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
