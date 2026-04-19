package com.sy.side.stock.application.dto.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockSearchCommand {
    private final String q;
    private final String mrktCtg;
    private final String activeYn;
    private final String basDt;
    private final int size;
    private final boolean fuzzy;
}