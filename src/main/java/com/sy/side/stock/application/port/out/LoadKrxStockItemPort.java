package com.sy.side.stock.application.port.out;

import com.sy.side.search.api.dto.response.KrxListedInfoResponse;

public interface LoadKrxStockItemPort {

    KrxListedInfoResponse fetch(String basDt, int pageNo);
}