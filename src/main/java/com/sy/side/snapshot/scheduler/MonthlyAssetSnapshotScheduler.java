package com.sy.side.snapshot.scheduler;

import com.sy.side.snapshot.application.port.in.CreateMonthlyAssetSnapshotUseCase;
import com.sy.side.snapshot.application.port.in.CreateMonthlyAssetSnapshotUseCase.SnapshotResult;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonthlyAssetSnapshotScheduler {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter SNAPSHOT_YM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private final CreateMonthlyAssetSnapshotUseCase createMonthlyAssetSnapshotUseCase;

    /**
     * 월별 자산 스냅샷 생성
     * 매월 1일 오전 12시 30분 실행 (KST)
     * - 직전 달 종가 기준으로 회원 전체 활성 계좌의 자산을 기록
     * - 같은 년월에 재실행되어도 멱등하게 덮어쓴다
     */
    @Scheduled(cron = "0 30 0 1 * *", zone = "Asia/Seoul")
    public void createPreviousMonthSnapshot() {
        String snapshotYm = YearMonth.from(LocalDate.now(KST))
                .minusMonths(1)
                .format(SNAPSHOT_YM_FORMATTER);

        log.info("[ASSET_SNAPSHOT_SCHEDULER] start. snapshotYm={}", snapshotYm);

        SnapshotResult result = createMonthlyAssetSnapshotUseCase.createSnapshot(snapshotYm);

        log.info(
                "[ASSET_SNAPSHOT_SCHEDULER] done. snapshotYm={}, target={}, success={}, fail={}",
                snapshotYm,
                result.targetAccountCount(),
                result.successCount(),
                result.failCount()
        );
    }
}
