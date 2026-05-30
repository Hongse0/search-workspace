package com.sy.side.snapshot.application.port.out;

import com.sy.side.snapshot.domain.AccountWeeklyAssetSnapshot;
import com.sy.side.snapshot.dto.response.WeeklyAssetSnapshotHistoryResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeeklyAssetSnapshotQueryPort {
    Optional<AccountWeeklyAssetSnapshot> findByAccountIdAndSnapshotDate(Long accountId, LocalDate snapshotDate);

    List<WeeklyAssetSnapshotHistoryResponse.WeeklyItem> findWeeklyAggregate(Long memberId, LocalDate fromDate);
}
