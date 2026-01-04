package com.sy.side.search.api.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockSyncResponse {

    private int syncedCount;
    private String indexName;
    private List<String> indexedFields;
}
