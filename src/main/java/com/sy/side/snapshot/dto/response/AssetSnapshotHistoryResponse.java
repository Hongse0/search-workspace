package com.sy.side.snapshot.dto.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AssetSnapshotHistoryResponse {

    private List<MonthlyItem> items;

    @Getter
    @Builder
    public static class MonthlyItem {
        private String snapshotYm;
        private BigDecimal cashBalance;
        private BigDecimal stockEvaluationAmount;
        private BigDecimal totalAssetValue;
        private Long holdingCount;
    }

    public static AssetSnapshotHistoryResponse empty() {
        return AssetSnapshotHistoryResponse.builder()
                .items(List.of())
                .build();
    }
}
