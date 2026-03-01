package com.sy.side.stock.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockSearchResponse {

    private String query;
    private int size;
    private long total;
    private List<SearchItem> items;

    @Getter
    @Builder
    public static class SearchItem {
        private String srtnCd;     // A005930
        private String isinCd;
        private String mrktCtg;    // KOSPI/KOSDAQ
        private String itmsNm;     // 삼성전자
        private String corpNm;     // 삼성전자(주)
        private String activeYn;   // Y/N
        private String basDt;      // 20260129
    }
}