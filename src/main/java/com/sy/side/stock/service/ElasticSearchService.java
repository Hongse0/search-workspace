package com.sy.side.stock.service;

import com.sy.side.stock.domain.StockItemMaster;
import com.sy.side.stock.dto.StockItemDocument;
import com.sy.side.stock.repository.StockItemMasterRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ElasticSearchService {

    private final StockItemMasterRepo stockItemMasterRepo;
    private final ElasticsearchOperations elasticsearchOperations;

    private static final String ES_INDEX = "stock_item_master";
    private static final int PER_PAGE = 1000;

    @Transactional(readOnly = true)
    public void syncEs(String basDt) {

        int page = 0;
        long totalIndexed = 0;

        while (true) {
            org.springframework.data.domain.Page<StockItemMaster> result =
                    stockItemMasterRepo.findByBasDt(basDt, PageRequest.of(page, PER_PAGE));

            if (result.isEmpty()) break;

            List<IndexQuery> queries = result.getContent().stream()
                    .map(StockItemDocument::from)
                    .map(doc -> new IndexQueryBuilder()
                            .withId(doc.getSrtnCd())
                            .withObject(doc)
                            .build())
                    .toList();

            elasticsearchOperations.bulkIndex(queries, IndexCoordinates.of(ES_INDEX));
            totalIndexed += queries.size();

            log.info("[ES Sync] basDt={}, page={}, size={}, totalIndexed={}",
                    basDt, page, queries.size(), totalIndexed);

            if (!result.hasNext()) break;
            page++;
        }

        elasticsearchOperations.indexOps(IndexCoordinates.of(ES_INDEX)).refresh();
        log.info("[ES Sync DONE] basDt={}, totalIndexed={}", basDt, totalIndexed);
    }

}
