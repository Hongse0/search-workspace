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
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "account_weekly_asset_snapshot",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_weekly_snapshot_account_date",
                        columnNames = {"account_id", "snapshot_date"}
                )
        },
        indexes = {
                @Index(name = "idx_weekly_snapshot_member_date", columnList = "member_id, snapshot_date"),
                @Index(name = "idx_weekly_snapshot_date", columnList = "snapshot_date")
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AccountWeeklyAssetSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weekly_snapshot_id")
    private Long weeklySnapshotId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    /** 주간 스냅샷 기준일. 매주 일요일 날짜를 저장한다. */
    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

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

    public static AccountWeeklyAssetSnapshot create(
            Long memberId,
            Long accountId,
            LocalDate snapshotDate,
            BigDecimal cashBalance,
            BigDecimal stockEvaluationAmount,
            Long holdingCount
    ) {
        BigDecimal cash = normalize(cashBalance);
        BigDecimal stock = normalize(stockEvaluationAmount);

        return AccountWeeklyAssetSnapshot.builder()
                .memberId(memberId)
                .accountId(accountId)
                .snapshotDate(snapshotDate)
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
