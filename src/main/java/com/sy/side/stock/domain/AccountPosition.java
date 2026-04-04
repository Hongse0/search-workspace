package com.sy.side.stock.domain;

import com.sy.side.account.domain.Account;
import com.sy.side.trade.domain.Market;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
                        name = "uk_account_stock_market",
                        columnNames = {"account_id", "stock_id", "market"}
                )
        },
        indexes = {
                @Index(name = "idx_position_account", columnList = "account_id"),
                @Index(name = "idx_position_stock", columnList = "stock_id"),
                @Index(name = "idx_position_account_market", columnList = "account_id, market")
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "account_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_pos_account")
    )
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "stock_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_pos_stock")
    )
    private StockItemMaster stock;

    @Enumerated(EnumType.STRING)
    @Column(name = "market", nullable = false, length = 10)
    private Market market;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "avg_price", precision = 18, scale = 4, nullable = false)
    private BigDecimal avgPrice;

    @Column(name = "cost_amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal costAmount;

    @Column(name = "realized_pnl", precision = 18, scale = 2, nullable = false)
    private BigDecimal realizedPnl;

    @Column(name = "last_trade_id")
    private Long lastTradeId;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.updatedAt = now;

        if (this.quantity == null) this.quantity = 0L;
        if (this.avgPrice == null) this.avgPrice = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        if (this.costAmount == null) this.costAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (this.realizedPnl == null) this.realizedPnl = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static AccountPosition open(
            Account account,
            StockItemMaster stock,
            Market market,
            Long quantity,
            BigDecimal totalCostAmount,
            Long lastTradeId
    ) {
        validateQuantity(quantity);

        BigDecimal normalizedCost = normalizeMoney(totalCostAmount);
        BigDecimal avgPrice = normalizedCost.divide(
                BigDecimal.valueOf(quantity),
                4,
                RoundingMode.HALF_UP
        );

        return AccountPosition.builder()
                .account(account)
                .stock(stock)
                .market(market)
                .quantity(quantity)
                .avgPrice(avgPrice)
                .costAmount(normalizedCost)
                .realizedPnl(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                .lastTradeId(lastTradeId)
                .build();
    }

    public void applyBuy(
            Long buyQuantity,
            BigDecimal buyCostAmount,
            Long tradeId
    ) {
        validateQuantity(buyQuantity);

        BigDecimal normalizedBuyCost = normalizeMoney(buyCostAmount);

        long newQuantity = this.quantity + buyQuantity;
        BigDecimal newCostAmount = this.costAmount.add(normalizedBuyCost).setScale(2, RoundingMode.HALF_UP);
        BigDecimal newAvgPrice = newCostAmount.divide(
                BigDecimal.valueOf(newQuantity),
                4,
                RoundingMode.HALF_UP
        );

        this.quantity = newQuantity;
        this.costAmount = newCostAmount;
        this.avgPrice = newAvgPrice;
        this.lastTradeId = tradeId;
    }

    private static void validateQuantity(Long quantity) {
        if (quantity == null || quantity <= 0L) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
    }

    private static BigDecimal normalizeMoney(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("금액은 0 이상이어야 합니다.");
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}