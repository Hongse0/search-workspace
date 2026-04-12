package com.sy.side.stock.application.port.out;

import com.sy.side.stock.dto.response.StockPriceApiResponse;
import java.util.List;

public interface StockPriceApiPort {
    List<StockPriceApiResponse.Item> fetchBySrtnCd(String srtnCd, String basDt);
}