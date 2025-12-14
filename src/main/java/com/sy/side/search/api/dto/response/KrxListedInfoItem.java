package com.sy.side.search.api.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KrxListedInfoItem(
        @JsonProperty("basDt") String basDt,       // YYYYMMDD
        @JsonProperty("srtnCd") String srtnCd,     // A000020
        @JsonProperty("isinCd") String isinCd,
        @JsonProperty("mrktCtg") String mrktCtg,   // KOSPI
        @JsonProperty("itmsNm") String itmsNm,
        @JsonProperty("crno") String crno,
        @JsonProperty("corpNm") String corpNm
) {}
