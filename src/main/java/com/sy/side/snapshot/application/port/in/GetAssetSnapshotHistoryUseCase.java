package com.sy.side.snapshot.application.port.in;

import com.sy.side.snapshot.dto.response.AssetSnapshotHistoryResponse;
import com.sy.side.snapshot.dto.response.WeeklyAssetSnapshotHistoryResponse;

public interface GetAssetSnapshotHistoryUseCase {
    AssetSnapshotHistoryResponse getMonthlyHistory(Long memberId, int months);

    WeeklyAssetSnapshotHistoryResponse getWeeklyHistory(Long memberId, int weeks);
}
