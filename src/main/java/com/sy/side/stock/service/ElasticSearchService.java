package com.sy.side.stock.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
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

    // todo : 예외처리 해줘야함
    @Transactional(readOnly = true)
    public void syncEs(String basDt) {

        int page = 0;
        long totalIndexed = 0;

        while (true) {
            var pageable = PageRequest.of(page, PER_PAGE);

            org.springframework.data.domain.Page<StockItemMaster> result =
                    stockItemMasterRepo.findByBasDt(basDt, pageable);

            if (result.isEmpty()) {
                log.info("[ES Sync STOP] empty page. basDt={}, page={}", basDt, page);
                break;
            }

            List<IndexQuery> queries = result.getContent().stream()
                    .map(StockItemDocument::from)
                    .map(doc -> new IndexQueryBuilder()
                            .withId(doc.getSrtnCd())
                            .withObject(doc)
                            .build())
                    .toList();


            elasticsearchOperations.bulkIndex(queries, IndexCoordinates.of(ES_INDEX));
            totalIndexed += queries.size();

            if (!result.hasNext()) break;
            page++;
        }
        log.info("[ES Sync DONE] index={}, basDt={}, totalIndexed={}", ES_INDEX, basDt, totalIndexed);
    }

}
