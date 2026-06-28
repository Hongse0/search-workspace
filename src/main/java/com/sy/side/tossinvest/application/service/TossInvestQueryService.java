package com.sy.side.tossinvest.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.sy.side.tossinvest.application.port.in.GetTossInvestAccountsUseCase;
import com.sy.side.tossinvest.application.port.in.GetTossInvestHoldingsUseCase;
import com.sy.side.tossinvest.application.port.in.GetTossInvestPricesUseCase;
import com.sy.side.tossinvest.application.port.out.TossInvestApiPort;
import com.sy.side.tossinvest.dto.response.TossInvestAccountResponse;
import com.sy.side.tossinvest.dto.response.TossInvestPriceResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TossInvestQueryService implements GetTossInvestAccountsUseCase,
        GetTossInvestHoldingsUseCase,
        GetTossInvestPricesUseCase {

    private final TossInvestApiPort tossInvestApiPort;

    @Override
    public TossInvestAccountResponse getAccounts() {
        return tossInvestApiPort.getAccounts();
    }

    @Override
    public JsonNode getHoldings(int accountSeq) {
        return tossInvestApiPort.getHoldings(accountSeq);
    }

    @Override
    public TossInvestPriceResponse getPrices(List<String> symbols) {
        return tossInvestApiPort.getPrices(symbols);
    }
}
