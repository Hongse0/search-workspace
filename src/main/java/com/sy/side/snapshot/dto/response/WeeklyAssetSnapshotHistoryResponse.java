package com.sy.side.snapshot.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeeklyAssetSnapshotHistoryResponse {

    private List<WeeklyItem> items;

    @Getter
    @Builder
    public static class WeeklyItem {
        private LocalDate snapshotDate;
        private BigDecimal cashBalance;
        private BigDecimal stockEvaluationAmount;
        private BigDecimal totalAssetValue;
        private Long holdingCount;
    }

    public static WeeklyAssetSnapshotHistoryResponse empty() {
        return WeeklyAssetSnapshotHistoryResponse.builder()
                .items(Collections.emptyList())
                .build();
    }
}
