package com.sy.side.stock.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockPriceApiResponse {

    private Response response;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private Header header;
        private Body body;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        private Integer numOfRows;
        private Integer pageNo;
        private Integer totalCount;
        private Items items;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        private List<Item> item;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {

        @JsonProperty("basDt")
        private String basDt;

        @JsonProperty("srtnCd")
        private String srtnCd;

        @JsonProperty("isinCd")
        private String isinCd;

        @JsonProperty("itmsNm")
        private String itmsNm;

        @JsonProperty("mrktCtg")
        private String mrktCtg;

        @JsonProperty("clpr")
        private String clpr;

        @JsonProperty("vs")
        private String vs;

        @JsonProperty("fltRt")
        private String fltRt;

        @JsonProperty("mkp")
        private String mkp;

        @JsonProperty("hipr")
        private String hipr;

        @JsonProperty("lopr")
        private String lopr;

        @JsonProperty("trqu")
        private String trqu;

        @JsonProperty("trPrc")
        private String trPrc;

        @JsonProperty("lstgStCnt")
        private String lstgStCnt;

        @JsonProperty("mrktTotAmt")
        private String mrktTotAmt;
    }
}