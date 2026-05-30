package com.sy.side.snapshot.application.port.in;

import java.time.LocalDate;

public interface CreateWeeklyAssetSnapshotUseCase {
    /**
     * 모든 활성 계좌에 대해 해당 일요일 기준 주간 자산 스냅샷을 생성/갱신한다.
     * 같은 날짜에 다시 호출되면 멱등하게 덮어쓴다.
     */
    SnapshotResult createSnapshot(LocalDate snapshotDate);

    record SnapshotResult(int targetAccountCount, int successCount, int failCount) {}
}
