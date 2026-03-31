package com.sy.side.account.domain;

import com.sy.side.account.error.AccountErrorImpl;
import com.sy.side.common.exception.BizException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
        name = "account",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "ux_account_member_number",
                        columnNames = {"member_id", "broker_name", "account_number"}
                )
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "broker_name", nullable = false, length = 50)
    private String brokerName;

    @Column(name = "account_number", nullable = false, length = 50)
    private String accountNumber;

    @Column(name = "account_name", length = 50)
    private String accountName;

    @Column(name = "base_currency", nullable = false, length = 3)
    private String baseCurrency;

    @Column(name = "cash_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal cashBalance;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public static Account create(
            Long memberId,
            String brokerName,
            String accountNumber,
            String accountName,
            String baseCurrency,
            BigDecimal initialBalance
    ) {
        BigDecimal balance = initialBalance == null
                ? BigDecimal.ZERO
                : initialBalance.setScale(2, RoundingMode.DOWN);

        return Account.builder()
                .memberId(memberId)
                .brokerName(brokerName)
                .accountNumber(accountNumber)
                .accountName(accountName)
                .baseCurrency(baseCurrency == null ? "KRW" : baseCurrency)
                .cashBalance(balance)
                .active(true)
                .build();
    }

    public void validateOwner(Long memberId) {
        if (!this.memberId.equals(memberId)) {
            throw new BizException(AccountErrorImpl.ACCOUNT_FORBIDDEN);
        }
    }

    public void validateActive() {
        if (!this.active) {
            throw new BizException(AccountErrorImpl.ACCOUNT_ERROR);
        }
    }

    public void decreaseCash(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException(AccountErrorImpl.ACCOUNT_ERROR);
        }

        if (this.cashBalance.compareTo(amount) < 0) {
            throw new BizException(AccountErrorImpl.ACCOUNT_ERROR);
        }

        this.cashBalance = this.cashBalance.subtract(amount);
    }

    public void increaseCash(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException(AccountErrorImpl.ACCOUNT_ERROR);
        }

        this.cashBalance = this.cashBalance.add(amount);
    }

    public void deactivate() {
        this.active = false;
    }

    @PrePersist
    public void prePersist() {
        if (this.baseCurrency == null) {
            this.baseCurrency = "KRW";
        }
        if (this.cashBalance == null) {
            this.cashBalance = BigDecimal.ZERO.setScale(2, RoundingMode.DOWN);
        } else {
            this.cashBalance = this.cashBalance.setScale(2, RoundingMode.DOWN);
        }
    }
}