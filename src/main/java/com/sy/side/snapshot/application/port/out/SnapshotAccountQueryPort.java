package com.sy.side.snapshot.application.port.out;

import com.sy.side.account.domain.Account;
import java.util.List;

public interface SnapshotAccountQueryPort {
    List<Account> findAllActiveAccounts();
}
