package com.sy.side.stock.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.PrefixQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.WildcardQuery;
import com.sy.side.stock.dto.request.StockSearchRequest;
import com.sy.side.stock.dto.response.StockSearchResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class StockSearchService {

    private static final String INDEX = "stock_item_master_v1";
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 50;

    private final ElasticsearchOperations es;

    /**
     * 종목 검색
     */
    public StockSearchResponse search(StockSearchRequest req) {
        String q = safeTrim(req.getQ());
        int size = normalizeSize(req.getSize(), DEFAULT_SIZE, MAX_SIZE);

        List<Query> shouldQueries = new ArrayList<>();

        if (hasText(q)) {
            // 정확히 일치 우선
            shouldQueries.add(functionBoost(termQuery("srtn_cd", q), 12.0f));
            shouldQueries.add(functionBoost(termQuery("itms_nm.keyword", q), 10.0f));
            shouldQueries.add(functionBoost(termQuery("corp_nm.keyword", q), 8.0f));

            // 일반 match
            shouldQueries.add(functionBoost(matchQuery("itms_nm", q), 4.0f));
            shouldQueries.add(functionBoost(matchQuery("corp_nm", q), 2.5f));

            // 부분 검색 보강
            shouldQueries.add(functionBoost(prefixQuery("itms_nm.keyword", q), 3.5f));
            shouldQueries.add(functionBoost(prefixQuery("corp_nm.keyword", q), 2.0f));
            shouldQueries.add(functionBoost(wildcardQuery("itms_nm.keyword", "*" + q + "*"), 1.8f));
            shouldQueries.add(functionBoost(wildcardQuery("corp_nm.keyword", "*" + q + "*"), 1.2f));

            if (req.isFuzzy()) {
                shouldQueries.add(functionBoost(matchQuery("itms_nm", q, true), 1.2f));
                shouldQueries.add(functionBoost(matchQuery("corp_nm", q, true), 1.0f));
            }
        }

        List<Query> filters = new ArrayList<>();

        if (hasText(req.getMrktCtg())) {
            filters.add(termQuery("mrkt_ctg", req.getMrktCtg().trim()));
        }
        if (hasText(req.getActiveYn())) {
            filters.add(termQuery("active_yn", req.getActiveYn().trim()));
        }
        if (hasText(req.getBasDt())) {
            filters.add(termQuery("bas_dt", req.getBasDt().trim()));
        }

        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

        if (!shouldQueries.isEmpty()) {
            boolBuilder.should(shouldQueries);
            boolBuilder.minimumShouldMatch("1");
        }

        if (!filters.isEmpty()) {
            boolBuilder.filter(filters);
        }

        Query finalQuery = new Query.Builder()
                .bool(boolBuilder.build())
                .build();

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
     * 자동완성
     *
     * 1) ac 서브필드가 있으면 가장 좋음
     * 2) 없을 수 있으므로 keyword prefix / wildcard fallback도 같이 둠
     */
    public StockSearchResponse autocomplete(String q, int size) {
        String keyword = safeTrim(q);
        int finalSize = normalizeSize(size, DEFAULT_SIZE, MAX_SIZE);

        if (!hasText(keyword)) {
            return StockSearchResponse.builder()
                    .query(keyword)
                    .size(finalSize)
                    .total(0L)
                    .items(List.of())
                    .build();
        }

        List<Query> shouldQueries = new ArrayList<>();

        // ac 서브필드가 있을 경우 우선 사용
        shouldQueries.add(functionBoost(matchQuery("itms_nm.ac", keyword), 5.0f));
        shouldQueries.add(functionBoost(matchQuery("corp_nm.ac", keyword), 3.0f));

        // fallback
        shouldQueries.add(functionBoost(prefixQuery("itms_nm.keyword", keyword), 4.0f));
        shouldQueries.add(functionBoost(prefixQuery("corp_nm.keyword", keyword), 2.5f));
        shouldQueries.add(functionBoost(wildcardQuery("itms_nm.keyword", "*" + keyword + "*"), 1.8f));
        shouldQueries.add(functionBoost(wildcardQuery("corp_nm.keyword", "*" + keyword + "*"), 1.2f));

        BoolQuery boolQuery = new BoolQuery.Builder()
                .should(shouldQueries)
                .minimumShouldMatch("1")
                .build();

        Query finalQuery = new Query.Builder()
                .bool(boolQuery)
                .build();

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
     * 단축코드 조회
     */
    public StockSearchResponse.SearchItem getBySrtnCd(String srtnCd) {
        String code = safeTrim(srtnCd);

        NativeQuery query = NativeQuery.builder()
                .withQuery(termQuery("srtn_cd", code))
                .withPageable(PageRequest.of(0, 1))
                .build();

        SearchHits<Map> hits = es.search(query, Map.class, IndexCoordinates.of(INDEX));
        if (hits.isEmpty()) {
            throw new IllegalArgumentException("Not found: " + code);
        }

        return toItem(hits.getSearchHit(0));
    }

    // ----------------------------------------------------------------
    // Query builders
    // ----------------------------------------------------------------

    private Query termQuery(String field, String value) {
        TermQuery tq = new TermQuery.Builder()
                .field(field)
                .value(value)
                .build();
        return new Query.Builder().term(tq).build();
    }

    private Query prefixQuery(String field, String value) {
        PrefixQuery pq = new PrefixQuery.Builder()
                .field(field)
                .value(value)
                .build();
        return new Query.Builder().prefix(pq).build();
    }

    private Query wildcardQuery(String field, String value) {
        WildcardQuery wq = new WildcardQuery.Builder()
                .field(field)
                .value(value)
                .build();
        return new Query.Builder().wildcard(wq).build();
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
                        m.prefixLength(1);
                        m.maxExpansions(20);
                    }
                    return m;
                })
                .build();
    }

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
                .srtnCd(asString(source.get("srtn_cd")))
                .isinCd(asString(source.get("isin_cd")))
                .mrktCtg(asString(source.get("mrkt_ctg")))
                .itmsNm(asString(source.get("itms_nm")))
                .corpNm(asString(source.get("corp_nm")))
                .activeYn(asString(source.get("active_yn")))
                .basDt(asString(source.get("bas_dt")))
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
        if (size <= 0) {
            return def;
        }
        return Math.min(size, max);
    }
}