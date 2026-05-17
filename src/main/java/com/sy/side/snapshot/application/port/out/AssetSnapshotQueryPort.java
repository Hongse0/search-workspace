package com.sy.side.snapshot.application.port.out;

import com.sy.side.snapshot.domain.AccountAssetSnapshot;
import com.sy.side.snapshot.dto.response.AssetSnapshotHistoryResponse;
import java.util.List;
import java.util.Optional;

public interface AssetSnapshotQueryPort {
    Optional<AccountAssetSnapshot> findByAccountIdAndSnapshotYm(Long accountId, String snapshotYm);

    List<AssetSnapshotHistoryResponse.MonthlyItem> findMonthlyAggregate(Long memberId, String fromYm);
}
