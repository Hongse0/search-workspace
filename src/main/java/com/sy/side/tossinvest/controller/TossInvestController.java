package com.sy.side.tossinvest.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.sy.side.tossinvest.application.port.in.GetTossInvestAccountsUseCase;
import com.sy.side.tossinvest.application.port.in.GetTossInvestHoldingsUseCase;
import com.sy.side.tossinvest.application.port.in.GetTossInvestPricesUseCase;
import com.sy.side.tossinvest.dto.response.TossInvestAccountResponse;
import com.sy.side.tossinvest.dto.response.TossInvestPriceResponse;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/toss-invest")
public class TossInvestController {

    private static final int MAX_PRICE_SYMBOL_COUNT = 200;
    private static final Pattern SYMBOL_PATTERN = Pattern.compile("^[A-Za-z0-9.\\-]+$");

    private final GetTossInvestAccountsUseCase getTossInvestAccountsUseCase;
    private final GetTossInvestHoldingsUseCase getTossInvestHoldingsUseCase;
    private final GetTossInvestPricesUseCase getTossInvestPricesUseCase;

    @GetMapping("/accounts")
    public TossInvestAccountResponse getAccounts() {
        return getTossInvestAccountsUseCase.getAccounts();
    }

    @GetMapping("/holdings")
    public JsonNode getHoldings(
            @RequestHeader("X-Tossinvest-Account") int accountSeq
    ) {
        return getTossInvestHoldingsUseCase.getHoldings(accountSeq);
    }

    @GetMapping("/prices")
    public TossInvestPriceResponse getPrices(
            @RequestParam List<String> symbols
    ) {
        List<String> normalizedSymbols = normalizeSymbols(symbols);
        return getTossInvestPricesUseCase.getPrices(normalizedSymbols);
    }

    private List<String> normalizeSymbols(List<String> symbols) {
        List<String> normalizedSymbols = symbols.stream()
                .flatMap(symbol -> Arrays.stream(symbol.split(",")))
                .map(String::trim)
                .filter(symbol -> !symbol.isBlank())
                .distinct()
                .toList();

        if (normalizedSymbols.isEmpty()) {
            throw new IllegalArgumentException("symbols는 필수입니다.");
        }

        if (normalizedSymbols.size() > MAX_PRICE_SYMBOL_COUNT) {
            throw new IllegalArgumentException("symbols는 최대 200개까지 조회할 수 있습니다.");
        }

        boolean hasInvalidSymbol = normalizedSymbols.stream()
                .anyMatch(symbol -> !SYMBOL_PATTERN.matcher(symbol).matches());

        if (hasInvalidSymbol) {
            throw new IllegalArgumentException("symbols는 영문, 숫자, '.', '-'만 사용할 수 있습니다.");
        }

        return normalizedSymbols;
    }
}
