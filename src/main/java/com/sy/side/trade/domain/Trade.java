package com.sy.side.trade.domain;

import com.sy.side.account.domain.Account;
import com.sy.side.stock.domain.StockItemMaster;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "trade",
        indexes = {
                @Index(
                        name = "idx_trade_account_datetime",
                        columnList = "account_id, trade_datetime"
                ),
                @Index(
                        name = "idx_trade_symbol",
                        columnList = "symbol, market"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_id")
    private Long tradeId;

    /** 계좌 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "account_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_trade_account")
    )
    private Account account;

    /** 주식 마스터 (없을 수도 있으므로 nullable) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "stock_id",
            foreignKey = @ForeignKey(name = "trade_stock_fk")
    )
    private StockItemMaster stock;

    /** 종목 코드 (AAPL, 005930 등) */
    @Column(name = "symbol", length = 30, nullable = false)
    private String symbol;

    /** 시장 (KR / US) */
    @Enumerated(EnumType.STRING)
    @Column(name = "market", nullable = false, length = 10)
    private Market market;

    /** 매수 / 매도 */
    @Enumerated(EnumType.STRING)
    @Column(name = "side", nullable = false, length = 10)
    private TradeSide side;

    /** 수량 */
    @Column(name = "quantity", nullable = false)
    private Long quantity;

    /** 체결 단가 */
    @Column(name = "price", precision = 18, scale = 4, nullable = false)
    private BigDecimal price;

    /** 수수료 */
    @Column(name = "fee", precision = 18, scale = 2, nullable = false)
    private BigDecimal fee;

    /** 세금 */
    @Column(name = "tax", precision = 18, scale = 2, nullable = false)
    private BigDecimal tax;

    /** 총 금액 (매수: -, 매도: + 또는 절대값 정책 가능) */
    @Column(name = "total_amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    /** 거래 일시 */
    @Column(name = "trade_datetime", nullable = false)
    private LocalDateTime tradeDateTime;

    /** 메모 */
    @Column(name = "memo", length = 255)
    private String memo;

    /** 생성 시각 */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
