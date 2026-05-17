package com.sy.side.snapshot.application.service;

import com.sy.side.snapshot.application.port.in.GetAssetSnapshotHistoryUseCase;
import com.sy.side.snapshot.application.port.out.AssetSnapshotQueryPort;
import com.sy.side.snapshot.dto.response.AssetSnapshotHistoryResponse;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAssetSnapshotHistoryService implements GetAssetSnapshotHistoryUseCase {

    private static final DateTimeFormatter SNAPSHOT_YM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final int DEFAULT_MONTHS = 12;
    private static final int MAX_MONTHS = 60;

    private final AssetSnapshotQueryPort assetSnapshotQueryPort;

    @Override
    public AssetSnapshotHistoryResponse getMonthlyHistory(Long memberId, int months) {
        int targetMonths = resolveMonths(months);
        String fromYm = YearMonth.from(LocalDate.now())
                .minusMonths(targetMonths - 1L)
                .format(SNAPSHOT_YM_FORMATTER);

        List<AssetSnapshotHistoryResponse.MonthlyItem> items =
                assetSnapshotQueryPort.findMonthlyAggregate(memberId, fromYm);

        if (items.isEmpty()) {
            return AssetSnapshotHistoryResponse.empty();
        }

        return AssetSnapshotHistoryResponse.builder()
                .items(items)
                .build();
    }

    private int resolveMonths(int months) {
        if (months <= 0) {
            return DEFAULT_MONTHS;
        }
        return Math.min(months, MAX_MONTHS);
    }
}
