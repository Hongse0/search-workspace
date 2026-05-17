package com.sy.side.snapshot.application.port.in;

public interface CreateMonthlyAssetSnapshotUseCase {
    /**
     * 모든 활성 계좌에 대해 해당 년월(YYYY-MM)의 자산 스냅샷을 생성/갱신한다.
     * 같은 년월에 다시 호출되면 멱등하게 덮어쓴다.
     */
    SnapshotResult createSnapshot(String snapshotYm);

    record SnapshotResult(int targetAccountCount, int successCount, int failCount) {}
}
