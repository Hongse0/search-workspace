package com.sy.side.stock.infrastructure.mapper;

import com.sy.side.stock.application.dto.result.StockSearchItemResult;
import com.sy.side.stock.application.dto.result.StockSearchResult;
import com.sy.side.stock.dto.StockSearchDocument;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;

@Component
public class StockSearchDocumentMapper {

    public StockSearchResult toSearchResult(String query, int size, SearchHits<StockSearchDocument> hits) {
        return StockSearchResult.builder()
                .query(query == null ? "" : query.trim())
                .size(size)
                .total(hits.getTotalHits())
                .items(hits.getSearchHits().stream()
                        .map(SearchHit::getContent)
                        .map(this::toItemResult)
                        .toList())
                .build();
    }

    public StockSearchItemResult toItemResult(StockSearchDocument doc) {
        return StockSearchItemResult.builder()
                .srtnCd(doc.getSrtnCd())
                .isinCd(doc.getIsinCd())
                .mrktCtg(doc.getMrktCtg())
                .itmsNm(doc.getItmsNm())
                .corpNm(doc.getCorpNm())
                .activeYn(doc.getActiveYn())
                .basDt(doc.getBasDt())
                .build();
    }
}
