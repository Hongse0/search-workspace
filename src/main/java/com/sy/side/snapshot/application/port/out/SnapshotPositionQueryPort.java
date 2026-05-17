package com.sy.side.snapshot.application.port.out;

import com.sy.side.position.domain.AccountPosition;
import java.util.List;

public interface SnapshotPositionQueryPort {
    List<AccountPosition> findAllByAccountId(Long accountId);
}
