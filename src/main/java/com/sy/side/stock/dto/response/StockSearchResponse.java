package com.sy.side.stock.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockSearchResponse {
    private String query;
    private int size;
    private long total;
    private List<StockSearchItemResponse> items;
}