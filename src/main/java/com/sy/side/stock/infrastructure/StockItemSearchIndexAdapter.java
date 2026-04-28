package com.sy.side.stock.infrastructure;

import com.sy.side.stock.application.port.out.IndexStockItemPort;
import com.sy.side.stock.domain.StockItemMaster;
import com.sy.side.stock.dto.StockItemDocument;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockItemSearchIndexAdapter implements IndexStockItemPort {

    private final ElasticsearchOperations elasticsearchOperations;

    private static final String ES_INDEX = "stock_item_master_v1";

    @Override
    public int bulkUpsert(List<StockItemMaster> stockItems) {
        if (stockItems == null || stockItems.isEmpty()) {
            return 0;
        }

        List<IndexQuery> queries = stockItems.stream()
                .map(StockItemDocument::from)
                .map(doc -> new IndexQueryBuilder()
                        .withId(doc.getSrtnCd())
                        .withObject(doc)
                        .build())
                .toList();

        elasticsearchOperations.bulkIndex(
                queries,
                IndexCoordinates.of(ES_INDEX)
        );

        log.info("[ES_BULK_UPSERT] index={}, size={}", ES_INDEX, queries.size());

        return queries.size();
    }
}
