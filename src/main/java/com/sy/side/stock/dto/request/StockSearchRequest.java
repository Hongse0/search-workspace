package com.sy.side.stock.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockSearchRequest {

    /** 검색어 */
    @NotBlank
    private String q;

    /** 결과 개수 */
    @Min(1) @Max(50)
    private int size = 10;

    /** 시장 필터: KOSPI/KOSDAQ/KONEX */
    private String mrktCtg;

    /** 활성여부(Y/N) */
    private String activeYn = "Y";

    /** 오타 허용(fuzzy) 사용 여부 */
    private boolean fuzzy = true;

    /** 기준일자(basDt) 필터 (optional) */
    private String basDt;
}
