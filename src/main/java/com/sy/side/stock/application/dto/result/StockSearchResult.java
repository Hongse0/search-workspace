package com.sy.side.stock.application.dto.result;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StockSearchResult {
    private final String query;
    private final int size;
    private final long total;
    private final List<StockSearchItemResult> items;
}