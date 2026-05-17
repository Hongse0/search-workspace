package com.sy.side.snapshot.application.port.out;

import com.sy.side.snapshot.domain.AccountAssetSnapshot;

public interface AssetSnapshotCommandPort {
    AccountAssetSnapshot save(AccountAssetSnapshot snapshot);
}
