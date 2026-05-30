package com.sy.side.snapshot.application.port.out;

import com.sy.side.snapshot.domain.AccountWeeklyAssetSnapshot;

public interface WeeklyAssetSnapshotCommandPort {
    AccountWeeklyAssetSnapshot save(AccountWeeklyAssetSnapshot snapshot);
}
