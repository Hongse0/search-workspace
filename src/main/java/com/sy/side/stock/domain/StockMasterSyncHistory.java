package com.sy.side.stock.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stock_master_sync_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockMasterSyncHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobName;

    private String basDt;

    @Enumerated(EnumType.STRING)
    private SyncStatus status;

    private Integer totalCount;

    private Integer savedCount;

    private Integer totalPages;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static StockMasterSyncHistory start(String jobName, String basDt) {
        LocalDateTime now = LocalDateTime.now();

        StockMasterSyncHistory history = new StockMasterSyncHistory();
        history.jobName = jobName;
        history.basDt = basDt;
        history.status = SyncStatus.STARTED;
        history.startedAt = now;
        history.createdAt = now;
        history.updatedAt = now;
        return history;
    }

    public void success(int totalCount, int savedCount, int totalPages) {
        this.status = SyncStatus.SUCCESS;
        this.totalCount = totalCount;
        this.savedCount = savedCount;
        this.totalPages = totalPages;
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