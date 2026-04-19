package com.sy.side.stock.infrastructure;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.PrefixQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.WildcardQuery;
import com.sy.side.stock.application.dto.command.StockAutocompleteCommand;
import com.sy.side.stock.application.dto.command.StockSearchCommand;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StockElasticsearchQueryFactory {

    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 50;

    public NativeQuery createSearchQuery(StockSearchCommand command) {
        String q = safeTrim(command.getQ());
        int size = normalizeSize(command.getSize(), DEFAULT_SIZE, MAX_SIZE);

        List<Query> shouldQueries = new ArrayList<>();
        List<Query> filters = new ArrayList<>();

        if (hasText(q)) {
            shouldQueries.add(functionBoost(termQuery("srtn_cd", q), 12.0f));
            shouldQueries.add(functionBoost(termQuery("itms_nm.keyword", q), 10.0f));
            shouldQueries.add(functionBoost(termQuery("corp_nm.keyword", q), 8.0f));

            shouldQueries.add(functionBoost(matchQuery("itms_nm", q, false), 4.0f));
            shouldQueries.add(functionBoost(matchQuery("corp_nm", q, false), 2.5f));

            shouldQueries.add(functionBoost(prefixQuery("itms_nm.keyword", q), 3.5f));
            shouldQueries.add(functionBoost(prefixQuery("corp_nm.keyword", q), 2.0f));

            shouldQueries.add(functionBoost(wildcardQuery("itms_nm.keyword", "*" + q + "*"), 1.8f));
            shouldQueries.add(functionBoost(wildcardQuery("corp_nm.keyword", "*" + q + "*"), 1.2f));

            if (command.isFuzzy()) {
                shouldQueries.add(functionBoost(matchQuery("itms_nm", q, true), 1.2f));
                shouldQueries.add(functionBoost(matchQuery("corp_nm", q, true), 1.0f));
            }
        }

        if (hasText(command.getMrktCtg())) {
            filters.add(termQuery("mrkt_ctg", command.getMrktCtg().trim()));
        }
        if (hasText(command.getActiveYn())) {
            filters.add(termQuery("active_yn", command.getActiveYn().trim()));
        }
        if (hasText(command.getBasDt())) {
            filters.add(termQuery("bas_dt", command.getBasDt().trim()));
        }

        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
        if (!shouldQueries.isEmpty()) {
            boolBuilder.should(shouldQueries);
            boolBuilder.minimumShouldMatch("1");
        }
        if (!filters.isEmpty()) {
            boolBuilder.filter(filters);
        }

        Query query = new Query.Builder()
                .bool(boolBuilder.build())
                .build();

        return NativeQuery.builder()
                .withQuery(query)
                .withPageable(PageRequest.of(0, size))
                .build();
    }

    public NativeQuery createAutocompleteQuery(StockAutocompleteCommand command) {
        String q = safeTrim(command.getQ());
        int size = normalizeSize(command.getSize(), DEFAULT_SIZE, MAX_SIZE);

        List<Query> shouldQueries = new ArrayList<>();
        shouldQueries.add(functionBoost(matchQuery("itms_nm.ac", q, false), 5.0f));
        shouldQueries.add(functionBoost(matchQuery("corp_nm.ac", q, false), 3.0f));
        shouldQueries.add(functionBoost(prefixQuery("itms_nm.keyword", q), 4.0f));
        shouldQueries.add(functionBoost(prefixQuery("corp_nm.keyword", q), 2.5f));
        shouldQueries.add(functionBoost(wildcardQuery("itms_nm.keyword", "*" + q + "*"), 1.8f));
        shouldQueries.add(functionBoost(wildcardQuery("corp_nm.keyword", "*" + q + "*"), 1.2f));

        Query query = new Query.Builder()
                .bool(new BoolQuery.Builder()
                        .should(shouldQueries)
                        .minimumShouldMatch("1")
                        .build())
                .build();

        return NativeQuery.builder()
                .withQuery(query)
                .withPageable(PageRequest.of(0, size))
                .build();
    }

    public NativeQuery createFindByCodeQuery(String srtnCd) {
        return NativeQuery.builder()
                .withQuery(termQuery("srtn_cd", safeTrim(srtnCd)))
                .withPageable(PageRequest.of(0, 1))
                .build();
    }

    private Query termQuery(String field, String value) {
        return new Query.Builder()
                .term(new TermQuery.Builder().field(field).value(value).build())
                .build();
    }

    private Query prefixQuery(String field, String value) {
        return new Query.Builder()
                .prefix(new PrefixQuery.Builder().field(field).value(value).build())
                .build();
    }

    private Query wildcardQuery(String field, String value) {
        return new Query.Builder()
                .wildcard(new WildcardQuery.Builder().field(field).value(value).build())
                .build();
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
                .functionScore(fs -> fs.query(inner).boost(boost))
                .build();
    }

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    private boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private int normalizeSize(int size, int def, int max) {
        if (size <= 0) return def;
        return Math.min(size, max);
    }
}
