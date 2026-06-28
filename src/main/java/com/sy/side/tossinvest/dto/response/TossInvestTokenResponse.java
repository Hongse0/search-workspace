package com.sy.side.tossinvest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TossInvestTokenResponse(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("expires_in")
        Long expiresIn,

        String scope
) {
}
