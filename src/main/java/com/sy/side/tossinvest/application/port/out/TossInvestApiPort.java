package com.sy.side.tossinvest.application.port.out;

import com.fasterxml.jackson.databind.JsonNode;
import com.sy.side.tossinvest.dto.response.TossInvestAccountResponse;
import com.sy.side.tossinvest.dto.response.TossInvestPriceResponse;
import java.util.List;

public interface TossInvestApiPort {

    TossInvestAccountResponse getAccounts();

    JsonNode getHoldings(int accountSeq);

    TossInvestPriceResponse getPrices(List<String> symbols);
}
