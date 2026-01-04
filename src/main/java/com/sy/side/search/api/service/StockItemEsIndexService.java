package com.sy.side.search.api.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import com.sy.side.search.api.dto.response.StockItemDocMin;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockItemEsIndexService {

    private final ElasticsearchClient esClient;

    private static final String INDEX = "stock_item";

    public int bulkUpsertMin(List<? extends StockItemMinSource> items) throws IOException {
        if (items == null || items.isEmpty()) return 0;

        BulkRequest.Builder br = new BulkRequest.Builder();

        for (var item : items) {
            String id = item.getSrtnCd();

            StockItemDocMin doc = StockItemDocMin.builder()
                    .srtn_cd(item.getSrtnCd())
                    .itms_nm(item.getItmsNm())
                    .mrkt_ctg(item.getMrktCtg())
                    .active_yn(item.getActiveYn())
                    .build();

            br.operations(op -> op.index(idx -> idx
                    .index(INDEX)
                    .id(id)
                    .document(doc)
            ));
        }

        BulkResponse resp = esClient.bulk(br.build());

        if (resp.errors()) {
            var failed = resp.items().stream()
                    .filter(i -> i.error() != null)
                    .map(i -> i.id() + " -> " + i.error().reason())
                    .toList();
            throw new IllegalStateException("ES bulk errors: " + failed);
        }

        return items.size();
    }

    /**
     * JPA Projection / DTO가 이 인터페이스만 구현하면 bulkUpsertMin에 바로 사용 가능
     */
    public interface StockItemMinSource {
        String getSrtnCd();
        String getItmsNm();
        String getMrktCtg();
        String getActiveYn();
    }
}
