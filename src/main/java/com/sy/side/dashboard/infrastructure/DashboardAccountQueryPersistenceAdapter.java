package com.sy.side.dashboard.infrastructure;

import com.sy.side.account.infrastructure.jpa.AccountRepository;
import com.sy.side.dashboard.application.port.out.DashboardAccountQueryPort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DashboardAccountQueryPersistenceAdapter implements DashboardAccountQueryPort {

    private final AccountRepository accountRepository;

    @Override
    public Optional<Long> findPrimaryAccountIdByMemberId(Long memberId) {
        return accountRepository.findFirstByMemberIdOrderByAccountIdAsc(memberId)
                .map(account -> account.getAccountId());
    }
}