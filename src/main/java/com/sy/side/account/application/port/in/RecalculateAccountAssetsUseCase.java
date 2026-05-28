package com.sy.side.account.application.port.in;

public interface RecalculateAccountAssetsUseCase {

    RecalculateResult recalculateAllActiveAccounts();

    record RecalculateResult(int requestedCount, int successCount, int failCount) {
    }
}
