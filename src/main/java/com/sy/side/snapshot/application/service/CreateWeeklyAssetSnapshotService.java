package com.sy.side.snapshot.application.service;

import com.sy.side.account.domain.Account;
import com.sy.side.snapshot.application.port.in.CreateWeeklyAssetSnapshotUseCase;
import com.sy.side.snapshot.application.port.out.SnapshotAccountQueryPort;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateWeeklyAssetSnapshotService implements CreateWeeklyAssetSnapshotUseCase {

    private final SnapshotAccountQueryPort snapshotAccountQueryPort;
    private final AccountAssetSnapshotPersister accountAssetSnapshotPersister;

    @Override
    public SnapshotResult createSnapshot(LocalDate snapshotDate) {
        if (snapshotDate == null || snapshotDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("주간 스냅샷 기준일은 일요일이어야 합니다.");
        }

        List<Account> accounts = snapshotAccountQueryPort.findAllActiveAccounts();

        int success = 0;
        int fail = 0;

        for (Account account : accounts) {
            try {
                accountAssetSnapshotPersister.saveOrUpdateWeekly(account, snapshotDate);
                success++;
            } catch (Exception e) {
                fail++;
                log.error(
                        "[WEEKLY_ASSET_SNAPSHOT] failed. accountId={}, snapshotDate={}",
                        account.getAccountId(),
                        snapshotDate,
                        e
                );
            }
        }

        return new SnapshotResult(accounts.size(), success, fail);
    }
}
