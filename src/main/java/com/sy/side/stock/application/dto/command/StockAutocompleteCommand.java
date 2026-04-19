package com.sy.side.stock.application.dto.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockAutocompleteCommand {
    private final String q;
    private final int size;
}