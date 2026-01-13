package com.sy.side.account.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "account",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "ux_account_member_broker_number",
                        columnNames = {"member_id", "broker_name", "account_number"}
                )
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(nullable = false)
    private Long memberId; // 다른 서비스의 member 테이블을 바라보는 FK 개념 (물리 FK는 안 걸어도 됨)

    @Column(nullable = false, length = 50)
    private String brokerName;

    @Column(nullable = false, length = 50)
    private String accountNumber;

    @Column(length = 50)
    private String accountName;

    @Column(nullable = false, length = 3)
    private String baseCurrency;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal cashBalance;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (cashBalance == null) {
            cashBalance = BigDecimal.ZERO;
        }
    }
}
