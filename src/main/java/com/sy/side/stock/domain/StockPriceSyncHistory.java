package com.sy.side.stock.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stock_price_sync_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockPriceSyncHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobName;

    private String basDt;

    @Enumerated(EnumType.STRING)
    private SyncStatus status;

    private Integer requestedCount;

    private Integer successCount;

    private Integer failCount;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static StockPriceSyncHistory start(String jobName, String basDt) {
        LocalDateTime now = LocalDateTime.now();

        StockPriceSyncHistory history = new StockPriceSyncHistory();
        history.jobName = jobName;
        history.basDt = basDt;
        history.status = SyncStatus.STARTED;
        history.startedAt = now;
        history.createdAt = now;
        history.updatedAt = now;
        return history;
    }

    public void success(int requestedCount, int successCount, int failCount) {
        this.status = SyncStatus.SUCCESS;
        this.requestedCount = requestedCount;
        this.successCount = successCount;
        this.failCount = failCount;
        this.endedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void fail(String errorMessage) {
        this.status = SyncStatus.FAILED;
        this.errorMessage = truncate(errorMessage);
        this.endedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    private String truncate(String message) {
        if (message == null) {
            return null;
        }
        return message.length() > 3000 ? message.substring(0, 3000) : message;
    }

    public enum SyncStatus {
        STARTED,
        SUCCESS,
        FAILED
    }
}