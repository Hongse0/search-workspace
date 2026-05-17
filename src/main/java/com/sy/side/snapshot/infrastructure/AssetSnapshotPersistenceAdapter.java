package com.sy.side.snapshot.infrastructure;

import com.sy.side.snapshot.application.port.out.AssetSnapshotCommandPort;
import com.sy.side.snapshot.application.port.out.AssetSnapshotQueryPort;
import com.sy.side.snapshot.domain.AccountAssetSnapshot;
import com.sy.side.snapshot.dto.response.AssetSnapshotHistoryResponse;
import com.sy.side.snapshot.infrastructure.jpa.AccountAssetSnapshotRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssetSnapshotPersistenceAdapter implements AssetSnapshotCommandPort, AssetSnapshotQueryPort {

    private final AccountAssetSnapshotRepository accountAssetSnapshotRepository;

    @Override
    public AccountAssetSnapshot save(AccountAssetSnapshot snapshot) {
        return accountAssetSnapshotRepository.save(snapshot);
    }

    @Override
    public Optional<AccountAssetSnapshot> findByAccountIdAndSnapshotYm(Long accountId, String snapshotYm) {
        return accountAssetSnapshotRepository.findByAccountIdAndSnapshotYm(accountId, snapshotYm);
    }

    @Override
    public List<AssetSnapshotHistoryResponse.MonthlyItem> findMonthlyAggregate(Long memberId, String fromYm) {
        return accountAssetSnapshotRepository.findMonthlyAggregateByMemberId(memberId, fromYm)
                .stream()
                .map(row -> AssetSnapshotHistoryResponse.MonthlyItem.builder()
                        .snapshotYm(row.getSnapshotYm())
                        .cashBalance(nullSafe(row.getCashBalance()))
                        .stockEvaluationAmount(nullSafe(row.getStockEvaluationAmount()))
                        .totalAssetValue(nullSafe(row.getTotalAssetValue()))
                        .holdingCount(row.getHoldingCount() == null ? 0L : row.getHoldingCount())
                        .build())
                .toList();
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
