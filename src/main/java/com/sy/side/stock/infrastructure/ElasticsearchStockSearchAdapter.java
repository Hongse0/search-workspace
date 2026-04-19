package com.sy.side.stock.infrastructure;

import com.sy.side.stock.application.dto.command.StockAutocompleteCommand;
import com.sy.side.stock.application.dto.command.StockSearchCommand;
import com.sy.side.stock.application.dto.result.StockSearchItemResult;
import com.sy.side.stock.application.dto.result.StockSearchResult;
import com.sy.side.stock.application.port.out.StockSearchPort;
import com.sy.side.stock.dto.StockSearchDocument;
import com.sy.side.stock.infrastructure.mapper.StockSearchDocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ElasticsearchStockSearchAdapter implements StockSearchPort {

    private static final String INDEX = "stock_item_master_v1";

    private final ElasticsearchOperations elasticsearchOperations;
    private final StockElasticsearchQueryFactory queryFactory;
    private final StockSearchDocumentMapper documentMapper;

    @Override
    public StockSearchResult search(StockSearchCommand command) {
        NativeQuery query = queryFactory.createSearchQuery(command);
        SearchHits<StockSearchDocument> hits = elasticsearchOperations.search(
                query,
                StockSearchDocument.class,
                IndexCoordinates.of(INDEX)
        );

        return documentMapper.toSearchResult(command.getQ(), command.getSize(), hits);
    }

    @Override
    public StockSearchResult autocomplete(StockAutocompleteCommand command) {
        NativeQuery query = queryFactory.createAutocompleteQuery(command);
        SearchHits<StockSearchDocument> hits = elasticsearchOperations.search(
                query,
                StockSearchDocument.class,
                IndexCoordinates.of(INDEX)
        );

        return documentMapper.toSearchResult(command.getQ(), command.getSize(), hits);
    }

    @Override
    public Optional<StockSearchItemResult> findBySrtnCd(String srtnCd) {
        NativeQuery query = queryFactory.createFindByCodeQuery(srtnCd);
        SearchHits<StockSearchDocument> hits = elasticsearchOperations.search(
                query,
                StockSearchDocument.class,
                IndexCoordinates.of(INDEX)
        );

        if (hits.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(documentMapper.toItemResult(hits.getSearchHit(0).getContent()));
    }
}
