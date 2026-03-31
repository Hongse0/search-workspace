package com.sy.side.stock.service;

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

    private static final String ES_INDEX = "stock_item_master_v1";
    private static final int PER_PAGE = 1000;

    @Transactional(readOnly = true)
    public void syncEs(String basDt) {

        int page = 0;
        long totalIndexed = 0;

        log.info("[ES Sync START] index={}, basDt={}", ES_INDEX, basDt);

        while (true) {
            var pageable = PageRequest.of(page, PER_PAGE);

            var result = stockItemMasterRepo.findByBasDt(basDt, pageable);

            log.info("[ES Sync PAGE] page={}, fetchedSize={}", page, result.getContent().size());

            if (result.isEmpty()) {
                log.info("[ES Sync STOP] empty page. basDt={}, page={}", basDt, page);
                break;
            }

            List<IndexQuery> queries = result.getContent().stream()
                    .map(StockItemDocument::from)
                    .map(doc -> new IndexQueryBuilder()
                            .withId(doc.getSrtnCd()) // 종목코드로 id 고정
                            .withObject(doc)
                            .build())
                    .toList();

            try {
                elasticsearchOperations.bulkIndex(queries, IndexCoordinates.of(ES_INDEX));
                totalIndexed += queries.size();

                log.info("[ES Sync BULK SUCCESS] page={}, size={}", page, queries.size());

            } catch (Exception e) {
                log.error("[ES Sync ERROR] page={}, message={}", page, e.getMessage(), e);
                throw e;
            }

            if (!result.hasNext()) break;
            page++;
        }

        log.info("[ES Sync DONE] index={}, basDt={}, totalIndexed={}", ES_INDEX, basDt, totalIndexed);
    }
}