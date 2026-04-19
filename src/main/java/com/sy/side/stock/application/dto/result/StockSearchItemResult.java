package com.sy.side.stock.application.dto.result;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockSearchItemResult {
    private final String srtnCd;
    private final String isinCd;
    private final String mrktCtg;
    private final String itmsNm;
    private final String corpNm;
    private final String activeYn;
    private final String basDt;
}