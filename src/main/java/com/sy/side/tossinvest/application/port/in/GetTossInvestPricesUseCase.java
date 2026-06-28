package com.sy.side.tossinvest.application.port.in;

import com.sy.side.tossinvest.dto.response.TossInvestPriceResponse;
import java.util.List;

public interface GetTossInvestPricesUseCase {

    TossInvestPriceResponse getPrices(List<String> symbols);
}
