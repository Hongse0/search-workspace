package com.sy.side.stock.domain;

import com.sy.side.account.entity.Account;
import com.sy.side.account.entity.Market;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "account_position",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_account_stock",
                        columnNames = {"account_id", "stock_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_position_account",
                        columnList = "account_id"
                ),
                @Index(
                        name = "idx_position_stock",
                        columnList = "stock_id"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AccountPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Long positionId;

    /** 계좌 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "account_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_pos_account")
    )
    private Account account;

    /** 종목 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "stock_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_pos_stock")
    )
    private StockItemMaster stock;

    /** 시장 (KR/US) */
    @Enumerated(EnumType.STRING)
    @Column(name = "market", nullable = false, length = 10)
    private Market market;

    /** 보유 수량 */
    @Column(name = "quantity", nullable = false)
    private Long quantity;

    /** 평균 매입 단가 */
    @Column(name = "avg_price", precision = 18, scale = 4, nullable = false)
    private BigDecimal avgPrice;

    /** 총 매입 원가 */
    @Column(name = "cost_amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal costAmount;

    /** 실현 손익 누적 */
    @Column(name = "realized_pnl", precision = 18, scale = 2, nullable = false)
    private BigDecimal realizedPnl;

    /** 마지막으로 반영한 trade_id (정합성/재처리 방지용) */
    @Column(name = "last_trade_id")
    private Long lastTradeId;

    /** 갱신 시각 */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** 낙관적 락 버전 */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.updatedAt = now;

        if (this.quantity == null) this.quantity = 0L;
        if (this.avgPrice == null) this.avgPrice = BigDecimal.ZERO;
        if (this.costAmount == null) this.costAmount = BigDecimal.ZERO;
        if (this.realizedPnl == null) this.realizedPnl = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void applySnapshot(
            Long quantity,
            BigDecimal avgPrice,
            BigDecimal costAmount,
            BigDecimal realizedPnl,
            Long lastTradeId
    ) {
        this.quantity = quantity;
        this.avgPrice = avgPrice;
        this.costAmount = costAmount;
        this.realizedPnl = realizedPnl;
        this.lastTradeId = lastTradeId;
        this.updatedAt = LocalDateTime.now();
    }
}
