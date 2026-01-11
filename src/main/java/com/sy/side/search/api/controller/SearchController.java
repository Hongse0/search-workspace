package com.sy.side.search.api.controller;

import com.sy.side.search.api.dto.response.StockSyncResponse;
import com.sy.side.search.api.service.StockItemEsIndexService;
import com.sy.side.search.infrastructure.elasticSearch.StockItemMinView;
import com.sy.side.stock.repository.StockItemMasterRepo;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/search")
public class SearchController {
    private final StockItemMasterRepo stockItemMasterRepo;
    private final StockItemEsIndexService stockItemEsIndexService;

    /**
     * MySQL(stock_item_master) -> Elasticsearch(stock_item) 최소 필드로 동기화
     * 수동 실행용 (배치/스케줄러는 나중에)
     */
    @PostMapping("/stocks/sync")
    public StockSyncResponse syncStocksToEs() throws IOException {
        List<StockItemMinView> items = stockItemMasterRepo.findAllByActiveYn("Y");
        int synced = stockItemEsIndexService.bulkUpsertMin(items);

        return new StockSyncResponse(
                synced,
                "stock_item",
                List.of("srtn_cd", "itms_nm", "mrkt_ctg", "active_yn")
        );
    }

}
