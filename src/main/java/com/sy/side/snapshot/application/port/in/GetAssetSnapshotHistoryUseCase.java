package com.sy.side.snapshot.application.port.in;

import com.sy.side.snapshot.dto.response.AssetSnapshotHistoryResponse;

public interface GetAssetSnapshotHistoryUseCase {
    AssetSnapshotHistoryResponse getMonthlyHistory(Long memberId, int months);
}
