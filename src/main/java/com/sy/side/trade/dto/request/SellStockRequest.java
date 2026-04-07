package com.sy.side.trade.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellStockRequest {
    private Long accountId;
    private String symbolCode;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal fee;
    private BigDecimal tax;
    private LocalDateTime tradeDateTime;
    private String memo;
}