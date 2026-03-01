package com.sy.side.stock.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import com.sy.side.stock.dto.request.StockSearchRequest;
import com.sy.side.stock.dto.response.StockSearchResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class StockSearchService {

    private static final String INDEX = "stock_item_master";
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 50;

    private final ElasticsearchOperations es;

    public StockSearchResponse search(StockSearchRequest req) {
        String q = safeTrim(req.getQ());
        int size = normalizeSize(req.getSize(), DEFAULT_SIZE, MAX_SIZE);

        // ---------------------------
        // 1) SHOULD (scoring) queries
        // ---------------------------
        List<Query> shouldQueries = new ArrayList<>();

        shouldQueries.add(functionBoost(termQuery("itmsNm.keyword", q), 10.0f));
        shouldQueries.add(functionBoost(termQuery("corpNm.keyword", q), 8.0f));
        shouldQueries.add(functionBoost(matchQuery("itmsNm", q), 4.0f));
        shouldQueries.add(functionBoost(matchQuery("corpNm", q), 2.5f));

        if (req.isFuzzy()) {
            Query fuzzy = matchQuery("itmsNm", q, true);
            shouldQueries.add(functionBoost(fuzzy, 1.2f));
        }

        // ---------------------------
        // 2) FILTER (no scoring)
        // ---------------------------
        List<Query> filters = new ArrayList<>();

        if (hasText(req.getMrktCtg())) {
            filters.add(termQuery("mrktCtg", req.getMrktCtg().trim()));
        }
        if (hasText(req.getActiveYn())) {
            filters.add(termQuery("activeYn", req.getActiveYn().trim()));
        }
        if (hasText(req.getBasDt())) {
            filters.add(termQuery("basDt", req.getBasDt().trim()));
        }

        // ---------------------------
        // 3) FINAL BOOL
        // ---------------------------
        BoolQuery boolQuery = new BoolQuery.Builder()
                .should(shouldQueries)
                .minimumShouldMatch("1")
                .filter(filters)
                .build();

        Query finalQuery = new Query.Builder().bool(boolQuery).build();

        NativeQuery query = NativeQuery.builder()
                .withQuery(finalQuery)
                .withPageable(PageRequest.of(0, size))
                .build();

        SearchHits<Map> hits = es.search(query, Map.class, IndexCoordinates.of(INDEX));

        List<StockSearchResponse.SearchItem> items = mapHits(hits);

        return StockSearchResponse.builder()
                .query(q)
                .size(size)
                .total(hits.getTotalHits())
                .items(items)
                .build();
    }

    /**
     * 자동완성: edge_ngram 인덱싱된 *.ac 필드를 조회
     */
    public StockSearchResponse autocomplete(String q, int size) {
        String keyword = safeTrim(q);
        int finalSize = normalizeSize(size, DEFAULT_SIZE, MAX_SIZE);

        List<Query> shouldQueries = new ArrayList<>();
        shouldQueries.add(functionBoost(matchQuery("itmsNm.ac", keyword), 3.0f));
        shouldQueries.add(functionBoost(matchQuery("corpNm.ac", keyword), 1.5f));

        BoolQuery boolQuery = new BoolQuery.Builder()
                .should(shouldQueries)
                .minimumShouldMatch("1")
                .build();

        Query finalQuery = new Query.Builder().bool(boolQuery).build();

        NativeQuery query = NativeQuery.builder()
                .withQuery(finalQuery)
                .withPageable(PageRequest.of(0, finalSize))
                .build();

        SearchHits<Map> hits = es.search(query, Map.class, IndexCoordinates.of(INDEX));
        List<StockSearchResponse.SearchItem> items = mapHits(hits);

        return StockSearchResponse.builder()
                .query(keyword)
                .size(finalSize)
                .total(hits.getTotalHits())
                .items(items)
                .build();
    }

    /**
     * 단축코드(예: A005930) 단건 조회
     */
    public StockSearchResponse.SearchItem getBySrtnCd(String srtnCd) {
        String code = safeTrim(srtnCd);

        NativeQuery query = NativeQuery.builder()
                .withQuery(termQuery("srtnCd", code))
                .withPageable(PageRequest.of(0, 1))
                .build();

        SearchHits<Map> hits = es.search(query, Map.class, IndexCoordinates.of(INDEX));
        if (hits.isEmpty()) {
            throw new IllegalArgumentException("Not found: " + code);
        }
        return toItem(hits.getSearchHit(0));
    }

    // ----------------------------------------------------------------
    // Query builders (ELC)
    // ----------------------------------------------------------------

    private Query termQuery(String field, String value) {
        TermQuery tq = new TermQuery.Builder()
                .field(field)
                .value(value)
                .build();
        return new Query.Builder().term(tq).build();
    }

    private Query matchQuery(String field, String value) {
        return matchQuery(field, value, false);
    }

    private Query matchQuery(String field, String value, boolean fuzzy) {
        return new Query.Builder()
                .match(m -> {
                    m.field(field).query(value);
                    if (fuzzy) {
                        m.fuzziness("AUTO");
                        m.prefixLength(2);
                        m.maxExpansions(20);
                    }
                    return m;
                })
                .build();
    }

    /**
     * "이 쿼리는 점수를 더 세게 줘라" 목적.
     * ELC Query는 query 자체에 boost를 직관적으로 못 거는 경우가 있어
     * function_score로 감싸는 게 가장 안전함.
     */
    private Query functionBoost(Query inner, float boost) {
        return new Query.Builder()
                .functionScore(fs -> fs
                        .query(inner)
                        .boost(boost)
                )
                .build();
    }

    // ----------------------------------------------------------------
    // Mapping helpers
    // ----------------------------------------------------------------

    private List<StockSearchResponse.SearchItem> mapHits(SearchHits<Map> hits) {
        List<SearchHit<Map>> searchHits = hits.getSearchHits();
        List<StockSearchResponse.SearchItem> items = new ArrayList<>(searchHits.size());
        for (SearchHit<Map> hit : searchHits) {
            items.add(toItem(hit));
        }
        return items;
    }

    private StockSearchResponse.SearchItem toItem(SearchHit<Map> hit) {
        Map source = hit.getContent();

        return StockSearchResponse.SearchItem.builder()
                .srtnCd(asString(source.get("srtnCd")))
                .isinCd(asString(source.get("isinCd")))
                .mrktCtg(asString(source.get("mrktCtg")))
                .itmsNm(asString(source.get("itmsNm")))
                .corpNm(asString(source.get("corpNm")))
                .activeYn(asString(source.get("activeYn")))
                .basDt(asString(source.get("basDt")))
                .build();
    }

    private String asString(Object v) {
        return v == null ? null : String.valueOf(v);
    }

    private boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    private int normalizeSize(int size, int def, int max) {
        if (size <= 0) return def;
        return Math.min(size, max);
    }
}
