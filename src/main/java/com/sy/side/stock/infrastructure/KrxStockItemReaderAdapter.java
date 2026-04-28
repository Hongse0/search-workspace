package com.sy.side.stock.infrastructure;

import com.sy.side.search.api.dto.response.KrxListedInfoResponse;
import com.sy.side.search.infrastructure.krx.client.KrxListedInfoClient;
import com.sy.side.stock.application.port.out.LoadKrxStockItemPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KrxStockItemReaderAdapter implements LoadKrxStockItemPort {

    private final KrxListedInfoClient client;

    @Override
    public KrxListedInfoResponse fetch(String basDt, int pageNo) {
        return client.fetch(basDt, pageNo);
    }
}