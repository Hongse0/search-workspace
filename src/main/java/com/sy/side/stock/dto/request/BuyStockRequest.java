package com.sy.side.stock.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class BuyStockRequest {

    /** 계좌 ID */
    @NotNull(message = "주식 계좌는 필수입니다.")
    private Long accountId;

    /** 종목 코드 */
    @NotBlank(message = "주식 코드는 필수입니다.")
    @Size(max = 20, message = "주식 코드는 20자 이내여야 합니다.")
    private String symbolCode;

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