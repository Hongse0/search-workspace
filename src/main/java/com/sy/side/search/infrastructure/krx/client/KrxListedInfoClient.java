package com.sy.side.search.infrastructure.krx.client;

import com.sy.side.search.api.dto.response.KrxListedInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class KrxListedInfoClient {

    private final WebClient webClient;

    @Value("${krx.listed-info.service-key}")
    private String serviceKey;

    @Value("${krx.listed-info.num-of-rows:1000}")
    private int numOfRows;

    @Value("${krx.listed-info.result-type:json}")
    private String resultType;

    /**
     * 특정 기준일(basDt)의 종목정보를 page 단위로 조회
     */
    public KrxListedInfoResponse fetch(String basDt, int pageNo) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("apis.data.go.kr")
                        .path("/1160100/service/GetKrxListedInfoService/getItemInfo")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("resultType", resultType)
                        .queryParam("numOfRows", numOfRows)
                        .queryParam("pageNo", pageNo)
                        .queryParam("basDt", basDt)
                        .build())
                .retrieve()
                .bodyToMono(KrxListedInfoResponse.class)
                .block();
    }

}
