package com.sy.side.tossinvest.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.sy.side.tossinvest.application.port.out.TossInvestApiPort;
import com.sy.side.tossinvest.config.TossInvestProperties;
import com.sy.side.tossinvest.dto.response.TossInvestAccountResponse;
import com.sy.side.tossinvest.dto.response.TossInvestPriceResponse;
import com.sy.side.tossinvest.dto.response.TossInvestTokenResponse;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossInvestApiAdapter implements TossInvestApiPort {

    private static final long DEFAULT_TOKEN_EXPIRES_IN_SECONDS = 3600L;
    private static final long TOKEN_REFRESH_MARGIN_SECONDS = 60L;

    private final WebClient webClient;
    private final TossInvestProperties properties;
    private final Clock clock = Clock.systemUTC();

    private volatile CachedToken cachedToken;

    @Override
    public TossInvestAccountResponse getAccounts() {
        return webClient.get()
                .uri(properties.getBaseUrl() + "/api/v1/accounts")
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .retrieve()
                .bodyToMono(TossInvestAccountResponse.class)
                .block();
    }

    @Override
    public JsonNode getHoldings(int accountSeq) {
        return webClient.get()
                .uri(properties.getBaseUrl() + "/api/v1/holdings")
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .header("X-Tossinvest-Account", String.valueOf(accountSeq))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    @Override
    public TossInvestPriceResponse getPrices(List<String> symbols) {
        return webClient.get()
                .uri(UriComponentsBuilder
                        .fromUriString(properties.getBaseUrl() + "/api/v1/prices")
                        .queryParam("symbols", String.join(",", symbols))
                        .build()
                        .toUri())
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .retrieve()
                .bodyToMono(TossInvestPriceResponse.class)
                .block();
    }

    private String bearerToken() {
        return "Bearer " + getAccessToken();
    }

    private String getAccessToken() {
        CachedToken token = cachedToken;
        if (token != null && token.isUsable(clock)) {
            return token.accessToken();
        }

        synchronized (this) {
            token = cachedToken;
            if (token != null && token.isUsable(clock)) {
                return token.accessToken();
            }

            TossInvestTokenResponse response = requestAccessToken();
            long expiresIn = response.expiresIn() != null
                    ? response.expiresIn()
                    : DEFAULT_TOKEN_EXPIRES_IN_SECONDS;

            cachedToken = new CachedToken(
                    response.accessToken(),
                    Instant.now(clock).plusSeconds(expiresIn)
            );

            log.info("토스증권 access token 발급 완료. expiresIn={}", expiresIn);
            return cachedToken.accessToken();
        }
    }

    private TossInvestTokenResponse requestAccessToken() {
        return webClient.post()
                .uri(properties.getBaseUrl() + "/oauth2/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                        .with("client_id", properties.getClientId())
                        .with("client_secret", properties.getClientSecret()))
                .retrieve()
                .bodyToMono(TossInvestTokenResponse.class)
                .block();
    }

    private record CachedToken(
            String accessToken,
            Instant expiresAt
    ) {
        private boolean isUsable(Clock clock) {
            return accessToken != null
                    && !accessToken.isBlank()
                    && expiresAt.isAfter(Instant.now(clock).plusSeconds(TOKEN_REFRESH_MARGIN_SECONDS));
        }
    }
}
