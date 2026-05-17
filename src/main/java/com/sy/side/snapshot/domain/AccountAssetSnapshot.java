package com.sy.side.snapshot.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
        name = "account_asset_snapshot",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_snapshot_account_ym",
                        columnNames = {"account_id", "snapshot_ym"}
                )
        },
        indexes = {
                @Index(name = "idx_snapshot_member_ym", columnList = "member_id, snapshot_ym"),
                @Index(name = "idx_snapshot_ym", columnList = "snapshot_ym")
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AccountAssetSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "snapshot_id")
    private Long snapshotId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    /** 스냅샷 기준 년월 (YYYY-MM, 직전 달 종가 기준) */
    @Column(name = "snapshot_ym", nullable = false, length = 7)
    private String snapshotYm;

    @Column(name = "cash_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal cashBalance;

    @Column(name = "stock_evaluation_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal stockEvaluationAmount;

    @Column(name = "total_asset_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalAssetValue;

    @Column(name = "holding_count", nullable = false)
    private Long holdingCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static AccountAssetSnapshot create(
            Long memberId,
            Long accountId,
            String snapshotYm,
            BigDecimal cashBalance,
            BigDecimal stockEvaluationAmount,
            Long holdingCount
    ) {
        BigDecimal cash = normalize(cashBalance);
        BigDecimal stock = normalize(stockEvaluationAmount);

        return AccountAssetSnapshot.builder()
                .memberId(memberId)
                .accountId(accountId)
                .snapshotYm(snapshotYm)
                .cashBalance(cash)
                .stockEvaluationAmount(stock)
                .totalAssetValue(cash.add(stock))
                .holdingCount(holdingCount == null ? 0L : holdingCount)
                .build();
    }

    public void overwrite(
            BigDecimal cashBalance,
            BigDecimal stockEvaluationAmount,
            Long holdingCount
    ) {
        BigDecimal cash = normalize(cashBalance);
        BigDecimal stock = normalize(stockEvaluationAmount);

        this.cashBalance = cash;
        this.stockEvaluationAmount = stock;
        this.totalAssetValue = cash.add(stock);
        this.holdingCount = holdingCount == null ? 0L : holdingCount;
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    private static BigDecimal normalize(BigDecimal value) {
        BigDecimal safe = value == null ? BigDecimal.ZERO : value;
        return safe.setScale(2, RoundingMode.HALF_UP);
    }
}
