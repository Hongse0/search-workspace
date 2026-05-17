package com.sy.side.snapshot.infrastructure;

import com.sy.side.position.domain.AccountPosition;
import com.sy.side.position.infrastructure.jpa.AccountPositionRepository;
import com.sy.side.snapshot.application.port.out.SnapshotPositionQueryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SnapshotPositionQueryPersistenceAdapter implements SnapshotPositionQueryPort {

    private final AccountPositionRepository accountPositionRepository;

    @Override
    public List<AccountPosition> findAllByAccountId(Long accountId) {
        return accountPositionRepository.findAllByAccountId(accountId);
    }
}
