package com.sy.side.tossinvest.dto.response;

public record TossInvestPrice(
        String symbol,
        String timestamp,
        String lastPrice,
        String currency
) {
}
