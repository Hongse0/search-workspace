package com.sy.side.stock.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EtfPriceInfoItem {

    private String basDt;
    private String srtnCd;
    private String isinCd;
    private String itmsNm;

    private String clpr;
    private String vs;
    private String fltRt;

    private String nav;
    private String mkp;
    private String hipr;
    private String lopr;

    private String trqu;
    private String trPrc;
    private String mrktTotAmt;
    private String nPptTotAmt;
    private String stLstgCnt;

    private String bssIdxIdxNm;
    private String bssIdxClpr;
}