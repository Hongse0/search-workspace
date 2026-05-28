package com.sy.side.snapshot.infrastructure;

import com.sy.side.account.domain.Account;
import com.sy.side.account.infrastructure.jpa.AccountRepository;
import com.sy.side.snapshot.application.port.out.SnapshotAccountQueryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SnapshotAccountQueryPersistenceAdapter implements SnapshotAccountQueryPort {

    private final AccountRepository accountRepository;

    @Override
    public List<Account> findAllActiveAccounts() {
        return accountRepository.findAllByActiveTrueOrderByAccountIdAsc();
    }
}
