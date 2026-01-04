package com.sy.side.search.api.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockItemDocMin {
    private String srtn_cd;
    private String itms_nm;
    private String mrkt_ctg;
    private String active_yn;
}
