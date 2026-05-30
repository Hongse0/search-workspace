package com.sy.side.snapshot.scheduler;

import com.sy.side.snapshot.application.port.in.CreateWeeklyAssetSnapshotUseCase;
import com.sy.side.snapshot.application.port.in.CreateWeeklyAssetSnapshotUseCase.SnapshotResult;
import com.sy.side.stock.application.port.in.RunStockPriceSyncJobUseCase;
import com.sy.side.stock.application.port.in.RunStockPriceSyncJobUseCase.StockPriceSyncJobResult;
import com.sy.side.stock.util.StockUtil;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyAssetSnapshotScheduler {

    private static final String PRICE_SYNC_JOB_NAME = "WEEKLY_SNAPSHOT_STOCK_PRICE_SYNC";
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final RunStockPriceSyncJobUseCase runStockPriceSyncJobUseCase;
    private final CreateWeeklyAssetSnapshotUseCase createWeeklyAssetSnapshotUseCase;

    /**
     * 주별 자산 스냅샷 생성
     * 매주 일요일 오전 12시 30분 실행 (KST)
     * - 직전 금요일 기준 주식 가격을 먼저 동기화한다
     * - 가격 동기화 완료 이벤트 리스너가 현재 계좌 자산을 재계산한다
     * - 일요일 기준 회원 전체 활성 계좌의 자산을 기록한다
     * - 같은 날짜에 재실행되어도 멱등하게 덮어쓴다
     */
    @Scheduled(cron = "0 30 0 * * SUN", zone = "Asia/Seoul")
    public void createWeeklySnapshot() {
        String fridayBasDt = StockUtil.resolveKrxPriceBaseDate(LocalDateTime.now(KST));
        LocalDate snapshotDate = LocalDate.now(KST)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        log.info(
                "[WEEKLY_ASSET_SNAPSHOT_SCHEDULER] start. snapshotDate={}, priceBasDt={}",
                snapshotDate,
                fridayBasDt
        );

        StockPriceSyncJobResult priceSyncResult = runStockPriceSyncJobUseCase.run(PRICE_SYNC_JOB_NAME, fridayBasDt);

        SnapshotResult result = createWeeklyAssetSnapshotUseCase.createSnapshot(snapshotDate);

        log.info(
                "[WEEKLY_ASSET_SNAPSHOT_SCHEDULER] done. snapshotDate={}, priceBasDt={}, priceHistoryId={}, target={}, success={}, fail={}",
                snapshotDate,
                priceSyncResult.basDt(),
                priceSyncResult.historyId(),
                result.targetAccountCount(),
                result.successCount(),
                result.failCount()
        );
    }
}
