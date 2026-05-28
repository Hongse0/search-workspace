package com.sy.side.account.application.service;

import com.sy.side.account.application.port.in.RecalculateAccountAssetsUseCase;
import com.sy.side.account.application.port.out.AccountCommandPort;
import com.sy.side.account.domain.Account;
import com.sy.side.position.application.port.out.PositionQueryPort;
import com.sy.side.snapshot.application.port.out.SnapshotAccountQueryPort;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecalculateAccountAssetsService implements RecalculateAccountAssetsUseCase {

    private final SnapshotAccountQueryPort snapshotAccountQueryPort;
    private final PositionQueryPort positionQueryPort;
    private final AccountCommandPort accountCommandPort;

    @Override
    @Transactional
    public RecalculateResult recalculateAllActiveAccounts() {
        List<Account> accounts = snapshotAccountQueryPort.findAllActiveAccounts();

        int success = 0;
        int fail = 0;

        for (Account account : accounts) {
            try {
                BigDecimal stockAssetValue = positionQueryPort.sumStockAssetValueByAccountId(account.getAccountId());
                Long holdingCount = positionQueryPort.countHoldingByAccountId(account.getAccountId());

                account.updateAssetEvaluation(stockAssetValue, holdingCount);
                accountCommandPort.save(account);
                success++;
            } catch (Exception e) {
                fail++;
                log.error(
                        "[ACCOUNT_ASSET_RECALCULATION] failed. accountId={}",
                        account.getAccountId(),
                        e
                );
            }
        }

        return new RecalculateResult(accounts.size(), success, fail);
    }
}
