package com.sy.side.search.api.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KrxListedInfoResponse(
        Response response
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(
            Header header,
            Body body
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Header(
            String resultCode,
            String resultMsg
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Body(
            @JsonProperty("numOfRows") int numOfRows,
            @JsonProperty("pageNo") int pageNo,
            @JsonProperty("totalCount") int totalCount,
            Items items
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Items(
            List<KrxListedInfoItem> item
    ) {}
}
