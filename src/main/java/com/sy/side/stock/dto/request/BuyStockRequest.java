package com.sy.side.stock.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class BuyStockRequest {

    /** 계좌 ID */
    @NotNull(message = "주식 계좌는 필수입니다.")
    private Long accountId;

    /** 주식 마스터 ID */
    @NotNull(message = "주식을 선택하세요.")
    private Long stockId;

    /** 매수 수량 */
    @NotNull(message = "매수 수량은 필수입니다.")
    @Positive(message = "매수 수량은 1 이상이어야 합니다.")
    private Long quantity;

    /** 체결 단가 */
    @NotNull(message = "매수 단가는 필수입니다.")
    @Positive(message = "매수 단가는 0보다 커야 합니다.")
    private BigDecimal price;

    /** 수수료 (없으면 0 처리) */
    @PositiveOrZero(message = "수수료는 0 이상이어야 합니다.")
    private BigDecimal fee;

    /** 세금 (없으면 0 처리) */
    @PositiveOrZero(message = "세금은 0 이상이어야 합니다.")
    private BigDecimal tax;

    /** 체결 일시 (없으면 서버에서 now()) */
    private LocalDateTime tradeDateTime;

    /** 메모 */
    @Size(max = 255, message = "메모는 255자 이내로 입력하세요.")
    private String memo;
}
