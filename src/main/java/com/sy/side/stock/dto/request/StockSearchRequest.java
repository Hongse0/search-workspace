package com.sy.side.stock.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockSearchRequest {
    private String q;
    private String mrktCtg;
    private String activeYn;
    private String basDt;
    private int size = 10;
    private boolean fuzzy;
}
