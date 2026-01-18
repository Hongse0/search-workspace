package com.sy.side.account.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AccountCreateRequest {

    @NotBlank(message = "증권사명은 필수입니다.")
    private String brokerName;

    @NotBlank(message = "계좌번호는 필수입니다.")
    @Size(max = 50, message = "계좌번호는 50자를 넘을 수 없습니다.")
    private String accountNumber;

    @Size(max = 50, message = "계좌 별칭은 50자를 넘을 수 없습니다.")
    private String accountName;

    @NotBlank(message = "기준 통화는 필수입니다. (예: KRW, USD)")
    @Size(min = 3, max = 3, message = "통화 코드는 3자리여야 합니다.")
    private String baseCurrency;

    @DecimalMin(value = "0.00", inclusive = true, message = "기본 자금은 0 이상이어야 합니다.")
    private java.math.BigDecimal initialBalance;
}
