package com.sy.side.tossinvest.application.port.in;

import com.fasterxml.jackson.databind.JsonNode;

public interface GetTossInvestHoldingsUseCase {

    JsonNode getHoldings(int accountSeq);
}
