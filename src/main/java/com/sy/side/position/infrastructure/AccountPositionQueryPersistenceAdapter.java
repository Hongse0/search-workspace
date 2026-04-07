package com.sy.side.position.infrastructure;

import com.sy.side.position.application.port.out.AccountPositionQueryPort;
import com.sy.side.position.infrastructure.jpa.AccountPositionRepository;
import com.sy.side.trade.dto.AccountPositionSummary;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountPositionQueryPersistenceAdapter implements AccountPositionQueryPort {

    private final AccountPositionRepository accountPositionRepository;

    @Override
    public Optional<AccountPositionSummary> findByAccountIdAndStockId(Long accountId, Long stockId) {
        return accountPositionRepository.findByAccountIdAndStockId(accountId, stockId)
                .map(position -> new AccountPositionSummary(
                        position.getAccount().getAccountId(),
                        position.getStock().getId(),
                        Math.toIntExact(position.getQuantity()),
                        position.getAvgPrice()
                ));
    }
}