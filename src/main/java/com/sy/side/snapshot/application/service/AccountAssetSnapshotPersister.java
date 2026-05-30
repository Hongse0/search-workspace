package com.sy.side.snapshot.application.service;

import com.sy.side.account.domain.Account;
import com.sy.side.position.domain.AccountPosition;
import com.sy.side.snapshot.application.port.out.AssetSnapshotCommandPort;
import com.sy.side.snapshot.application.port.out.AssetSnapshotQueryPort;
import com.sy.side.snapshot.application.port.out.SnapshotPositionQueryPort;
import com.sy.side.snapshot.application.port.out.SnapshotStockPriceQueryPort;
import com.sy.side.snapshot.application.port.out.WeeklyAssetSnapshotCommandPort;
import com.sy.side.snapshot.application.port.out.WeeklyAssetSnapshotQueryPort;
import com.sy.side.snapshot.domain.AccountAssetSnapshot;
import com.sy.side.snapshot.domain.AccountWeeklyAssetSnapshot;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AccountAssetSnapshotPersister {

    private final SnapshotPositionQueryPort snapshotPositionQueryPort;
    private final SnapshotStockPriceQueryPort snapshotStockPriceQueryPort;
    private final AssetSnapshotCommandPort assetSnapshotCommandPort;
    private final AssetSnapshotQueryPort assetSnapshotQueryPort;
    private final WeeklyAssetSnapshotCommandPort weeklyAssetSnapshotCommandPort;
    private final WeeklyAssetSnapshotQueryPort weeklyAssetSnapshotQueryPort;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveOrUpdate(Account account, String snapshotYm) {
        List<AccountPosition> positions = snapshotPositionQueryPort.findAllByAccountId(account.getAccountId());

        BigDecimal stockEvaluation = calculateStockEvaluation(positions);
        long holdingCount = positions.size();

        assetSnapshotQueryPort.findByAccountIdAndSnapshotYm(account.getAccountId(), snapshotYm)
                .ifPresentOrElse(
                        existing -> existing.overwrite(account.getCashBalance(), stockEvaluation, holdingCount),
                        () -> assetSnapshotCommandPort.save(
                                AccountAssetSnapshot.create(
                                        account.getMemberId(),
                                        account.getAccountId(),
                                        snapshotYm,
                                        account.getCashBalance(),
                                        stockEvaluation,
                                        holdingCount
                                )
                        )
                );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveOrUpdateWeekly(Account account, LocalDate snapshotDate) {
        List<AccountPosition> positions = snapshotPositionQueryPort.findAllByAccountId(account.getAccountId());

        BigDecimal stockEvaluation = calculateStockEvaluation(positions);
        long holdingCount = positions.size();

        weeklyAssetSnapshotQueryPort.findByAccountIdAndSnapshotDate(account.getAccountId(), snapshotDate)
                .ifPresentOrElse(
                        existing -> existing.overwrite(account.getCashBalance(), stockEvaluation, holdingCount),
                        () -> weeklyAssetSnapshotCommandPort.save(
                                AccountWeeklyAssetSnapshot.create(
                                        account.getMemberId(),
                                        account.getAccountId(),
                                        snapshotDate,
                                        account.getCashBalance(),
                                        stockEvaluation,
                                        holdingCount
                                )
                        )
                );
    }

    private BigDecimal calculateStockEvaluation(List<AccountPosition> positions) {
        if (positions.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Set<Long> stockIds = positions.stream()
                .map(position -> position.getStock().getId())
                .collect(Collectors.toSet());

        Map<Long, BigDecimal> latestPriceMap = snapshotStockPriceQueryPort.findLatestPriceMap(stockIds);

        return positions.stream()
                .map(position -> {
                    BigDecimal currentPrice = latestPriceMap.getOrDefault(
                            position.getStock().getId(),
                            BigDecimal.ZERO
                    );
                    long quantity = position.getQuantity() == null ? 0L : position.getQuantity();
                    return currentPrice.multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
