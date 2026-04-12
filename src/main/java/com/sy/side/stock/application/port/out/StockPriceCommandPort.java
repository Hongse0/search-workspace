package com.sy.side.stock.application.port.out;

import com.sy.side.stock.domain.StockPriceDaily;
import java.util.Optional;

public interface StockPriceCommandPort {
    StockPriceDaily save(StockPriceDaily stockPriceDaily);
    Optional<StockPriceDaily> findBySrtnCdAndBasDt(String srtnCd, String basDt);
}