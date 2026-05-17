package com.sy.side.snapshot.application.service;

import com.sy.side.account.domain.Account;
import com.sy.side.snapshot.application.port.in.CreateMonthlyAssetSnapshotUseCase;
import com.sy.side.snapshot.application.port.out.SnapshotAccountQueryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateMonthlyAssetSnapshotService implements CreateMonthlyAssetSnapshotUseCase {

    private final SnapshotAccountQueryPort snapshotAccountQueryPort;
    private final AccountAssetSnapshotPersister accountAssetSnapshotPersister;

    @Override
    public SnapshotResult createSnapshot(String snapshotYm) {
        List<Account> accounts = snapshotAccountQueryPort.findAllActiveAccounts();

        int success = 0;
        int fail = 0;

        for (Account account : accounts) {
            try {
                accountAssetSnapshotPersister.saveOrUpdate(account, snapshotYm);
                success++;
            } catch (Exception e) {
                fail++;
                log.error(
                        "[ASSET_SNAPSHOT] failed. accountId={}, snapshotYm={}",
                        account.getAccountId(),
                        snapshotYm,
                        e
                );
            }
        }

        return new SnapshotResult(accounts.size(), success, fail);
    }
}
