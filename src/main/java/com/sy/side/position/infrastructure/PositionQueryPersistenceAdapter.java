package com.sy.side.position.infrastructure;

import com.sy.side.position.application.port.out.PositionQueryPort;
import com.sy.side.position.infrastructure.jpa.AccountPositionRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PositionQueryPersistenceAdapter implements PositionQueryPort {

    private final AccountPositionRepository accountPositionRepository;

    @Override
    public BigDecimal sumStockAssetValueByAccountId(Long accountId) {
        BigDecimal result = accountPositionRepository.sumStockAssetValueByAccountId(accountId);
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public Long countHoldingByAccountId(Long accountId) {
        Long result = accountPositionRepository.countHoldingByAccountId(accountId);
        return result != null ? result : 0L;
    }
}