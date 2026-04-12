package com.sy.side.stock.infrastructure;

import com.sy.side.stock.application.port.out.StockPriceApiPort;
import com.sy.side.stock.dto.response.StockPriceApiResponse;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublicStockPriceApiAdapter implements StockPriceApiPort {

    private final RestTemplate restTemplate;

    @Value("${external.public-data.stock.service-key}")
    private String serviceKey;

    @Value("${external.public-data.stock.base-url}")
    private String baseUrl;

    @Override
    public List<StockPriceApiResponse.Item> fetchBySrtnCd(String srtnCd, String basDt) {
        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/getStockPriceInfo")
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", 10)
                .queryParam("pageNo", 1)
                .queryParam("resultType", "json")
                .queryParam("likeSrtnCd", srtnCd)
                .queryParamIfPresent("basDt", java.util.Optional.ofNullable(
                        basDt != null && !basDt.isBlank() ? basDt : null
                ))
                .build(false)
                .toUriString();

        log.info("공공 주식시세 API 요청. srtnCd={}, basDt={}, url={}", srtnCd, basDt, maskServiceKey(url));

        StockPriceApiResponse response = restTemplate.getForObject(url, StockPriceApiResponse.class);

        if (response == null
                || response.getResponse() == null
                || response.getResponse().getHeader() == null
                || response.getResponse().getBody() == null
                || response.getResponse().getBody().getItems() == null
                || response.getResponse().getBody().getItems().getItem() == null) {
            return Collections.emptyList();
        }

        log.info("공공 주식시세 API 응답. resultCode={}, resultMsg={}",
                response.getResponse().getHeader().getResultCode(),
                response.getResponse().getHeader().getResultMsg());

        return response.getResponse().getBody().getItems().getItem().stream()
                .filter(item -> srtnCd.equals(item.getSrtnCd()))
                .toList();
    }

    private String maskServiceKey(String url) {
        return url.replaceAll("(serviceKey=)[^&]+", "$1****");
    }
}