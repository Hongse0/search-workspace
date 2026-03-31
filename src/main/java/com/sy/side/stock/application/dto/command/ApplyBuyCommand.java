package com.sy.side.stock.application.dto.command;

import com.sy.side.trade.domain.Market;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ApplyBuyCommand {

    private final Long accountId;
    private final Long stockId;
    private final Market market;

    private final Long buyQuantity;
    private final BigDecimal buyPrice;

    private final BigDecimal fee;
    private final BigDecimal tax;

    private final Long tradeId;
    private final LocalDateTime tradeDateTime;

    public BigDecimal feeOrZero() {
        return fee != null ? fee : BigDecimal.ZERO;
    }

    public BigDecimal taxOrZero() {
        return tax != null ? tax : BigDecimal.ZERO;
    }

    public LocalDateTime tradeDateTimeOrNow() {
        return tradeDateTime != null ? tradeDateTime : LocalDateTime.now();
    }
}
