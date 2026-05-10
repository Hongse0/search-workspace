package com.sy.side.stock.infrastructure;

import com.sy.side.stock.application.port.out.LoadEtfPriceInfoPort;
import com.sy.side.stock.dto.response.EtfPriceInfoItem;
import com.sy.side.stock.dto.response.EtfPriceInfoResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class EtfPublicDataAdapter implements LoadEtfPriceInfoPort {

    private final RestTemplate restTemplate;

    @Value("${external.public-data.stock.service-key}")
    private String serviceKey;

    private static final String ETF_API_URL = "https://apis.data.go.kr/1160100/service/GetSecuritiesProductInfoService/getETFPriceInfo";

    private static final int NUM_OF_ROWS = 1000;
    private static final String RESULT_TYPE = "json";

    @Override
    public List<EtfPriceInfoItem> loadEtfPriceInfos(String basDt) {
        List<EtfPriceInfoItem> result = new ArrayList<>();

        int pageNo = 1;
        int totalCount = 0;

        do {
            EtfPriceInfoResponse response = requestPage(basDt, pageNo);

            if (!isValidResponse(response)) {
                log.warn("[ETF API 응답 비정상] basDt={}, pageNo={}", basDt, pageNo);
                return result;
            }

            log.info("[ETF API 응답] resultCode={}, resultMsg={}",
                    response.getResponse().getHeader().getResultCode(),
                    response.getResponse().getHeader().getResultMsg());

            validateResultCode(response, basDt, pageNo);

            EtfPriceInfoResponse.Body body = response.getResponse().getBody();
            totalCount = body.getTotalCount() == null ? 0 : body.getTotalCount();

            List<EtfPriceInfoItem> items = extractItems(response);
            result.addAll(items);

            log.info("[ETF API 조회] basDt={}, pageNo={}, pageCount={}, totalCount={}",
                    basDt, pageNo, items.size(), totalCount);

            pageNo++;

        } while ((pageNo - 1) * NUM_OF_ROWS < totalCount);

        return result;
    }

    private EtfPriceInfoResponse requestPage(String basDt, int pageNo) {
        String url = UriComponentsBuilder
                .fromHttpUrl(ETF_API_URL)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", NUM_OF_ROWS)
                .queryParam("pageNo", pageNo)
                .queryParam("resultType", RESULT_TYPE)
                .queryParamIfPresent("basDt", java.util.Optional.ofNullable(
                        basDt != null && !basDt.isBlank() ? basDt : null
                ))
                .build(false)
                .toUriString();

        log.info("[ETF API 요청] basDt={}, pageNo={}, url={}",
                basDt, pageNo, maskServiceKey(url));

        return restTemplate.getForObject(url, EtfPriceInfoResponse.class);
    }

    private boolean isValidResponse(EtfPriceInfoResponse response) {
        return response != null
                && response.getResponse() != null
                && response.getResponse().getHeader() != null
                && response.getResponse().getBody() != null;
    }

    private void validateResultCode(EtfPriceInfoResponse response, String basDt, int pageNo) {
        EtfPriceInfoResponse.Header header = response.getResponse().getHeader();

        if (!Objects.equals(header.getResultCode(), "00")) {
            throw new IllegalStateException(
                    "ETF API 호출 실패. resultCode=" + header.getResultCode()
                            + ", resultMsg=" + header.getResultMsg()
                            + ", basDt=" + basDt
                            + ", pageNo=" + pageNo
            );
        }
    }

    private List<EtfPriceInfoItem> extractItems(EtfPriceInfoResponse response) {
        EtfPriceInfoResponse.Body body = response.getResponse().getBody();

        if (body.getItems() == null || body.getItems().getItem() == null) {
            return Collections.emptyList();
        }

        return body.getItems().getItem();
    }

    private String maskServiceKey(String url) {
        return url.replaceAll("(serviceKey=)[^&]+", "$1****");
    }
}