package com.sy.side.stock.dto.response;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockSearchItemResponse {
    private String srtnCd;
    private String isinCd;
    private String mrktCtg;
    private String itmsNm;
    private String corpNm;
    private String activeYn;
    private String basDt;
}