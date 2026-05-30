package com.sy.side.snapshot.infrastructure;

import com.sy.side.snapshot.application.port.out.WeeklyAssetSnapshotCommandPort;
import com.sy.side.snapshot.application.port.out.WeeklyAssetSnapshotQueryPort;
import com.sy.side.snapshot.domain.AccountWeeklyAssetSnapshot;
import com.sy.side.snapshot.dto.response.WeeklyAssetSnapshotHistoryResponse;
import com.sy.side.snapshot.infrastructure.jpa.AccountWeeklyAssetSnapshotRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeeklyAssetSnapshotPersistenceAdapter implements WeeklyAssetSnapshotCommandPort, WeeklyAssetSnapshotQueryPort {

    private final AccountWeeklyAssetSnapshotRepository accountWeeklyAssetSnapshotRepository;

    @Override
    public AccountWeeklyAssetSnapshot save(AccountWeeklyAssetSnapshot snapshot) {
        return accountWeeklyAssetSnapshotRepository.save(snapshot);
    }

    @Override
    public Optional<AccountWeeklyAssetSnapshot> findByAccountIdAndSnapshotDate(Long accountId, LocalDate snapshotDate) {
        return accountWeeklyAssetSnapshotRepository.findByAccountIdAndSnapshotDate(accountId, snapshotDate);
    }

    @Override
    public List<WeeklyAssetSnapshotHistoryResponse.WeeklyItem> findWeeklyAggregate(Long memberId, LocalDate fromDate) {
        return accountWeeklyAssetSnapshotRepository.findWeeklyAggregateByMemberId(memberId, fromDate)
                .stream()
                .map(row -> WeeklyAssetSnapshotHistoryResponse.WeeklyItem.builder()
                        .snapshotDate(row.getSnapshotDate())
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
