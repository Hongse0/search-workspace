package com.sy.side.trade.dto;


import com.sy.side.trade.domain.Market;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApplySellCommand {
    private Long accountId;
    private Long stockId;
    private Market market;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal fee;
    private BigDecimal tax;
    private Long tradeId;
    private LocalDateTime tradeDateTime;
}
