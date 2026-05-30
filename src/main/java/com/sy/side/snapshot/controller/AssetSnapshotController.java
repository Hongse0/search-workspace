package com.sy.side.snapshot.controller;

import com.sy.side.common.annotation.UserParam;
import com.sy.side.common.entity.UserSession;
import com.sy.side.snapshot.application.port.in.CreateMonthlyAssetSnapshotUseCase;
import com.sy.side.snapshot.application.port.in.CreateWeeklyAssetSnapshotUseCase;
import com.sy.side.snapshot.application.port.in.GetAssetSnapshotHistoryUseCase;
import com.sy.side.snapshot.dto.response.AssetSnapshotHistoryResponse;
import com.sy.side.snapshot.dto.response.WeeklyAssetSnapshotHistoryResponse;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/snapshot")
@RequiredArgsConstructor
@Slf4j
public class AssetSnapshotController {

    private final GetAssetSnapshotHistoryUseCase getAssetSnapshotHistoryUseCase;
    private final CreateMonthlyAssetSnapshotUseCase createMonthlyAssetSnapshotUseCase;
    private final CreateWeeklyAssetSnapshotUseCase createWeeklyAssetSnapshotUseCase;

    /** 월별 자산 변화 조회 (최근 N개월) */
    @GetMapping("/monthly")
    public AssetSnapshotHistoryResponse getMonthlyHistory(
            @UserParam UserSession userSession,
            @RequestParam(name = "months", defaultValue = "12") int months
    ) {
        Long memberId = userSession.getMemberSession().getMemberId();
        return getAssetSnapshotHistoryUseCase.getMonthlyHistory(memberId, months);
    }

    /** 주별 자산 변화 조회 (최근 N주) */
    @GetMapping("/weekly")
    public WeeklyAssetSnapshotHistoryResponse getWeeklyHistory(
            @UserParam UserSession userSession,
            @RequestParam(name = "weeks", defaultValue = "12") int weeks
    ) {
        Long memberId = userSession.getMemberSession().getMemberId();
        return getAssetSnapshotHistoryUseCase.getWeeklyHistory(memberId, weeks);
    }

    /** 특정 년월 스냅샷 수동 생성 (운영용) */
    @PostMapping("/manual")
    public CreateMonthlyAssetSnapshotUseCase.SnapshotResult createManualSnapshot(
            @RequestParam(name = "snapshotYm") String snapshotYm
    ) {
        log.info("[ASSET_SNAPSHOT] manual trigger. snapshotYm={}", snapshotYm);
        return createMonthlyAssetSnapshotUseCase.createSnapshot(snapshotYm);
    }

    /** 특정 일요일 기준 주간 스냅샷 수동 생성 (운영용) */
    @PostMapping("/weekly/manual")
    public CreateWeeklyAssetSnapshotUseCase.SnapshotResult createWeeklyManualSnapshot(
            @RequestParam(name = "snapshotDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate snapshotDate
    ) {
        log.info("[WEEKLY_ASSET_SNAPSHOT] manual trigger. snapshotDate={}", snapshotDate);
        return createWeeklyAssetSnapshotUseCase.createSnapshot(snapshotDate);
    }

    /**
     * 월별 자산 변화 조회 - memberId 직접 지정
     * Postman / 로컬 테스트용
     */
    @GetMapping("/monthly/test")
    public AssetSnapshotHistoryResponse getMonthlyHistoryForTest(
            @RequestParam(name = "memberId") Long memberId,
            @RequestParam(name = "months", defaultValue = "12") int months
    ) {
        log.info("[ASSET_SNAPSHOT] test history query. memberId={}, months={}", memberId, months);
        return getAssetSnapshotHistoryUseCase.getMonthlyHistory(memberId, months);
    }

    /**
     * 주별 자산 변화 조회 - memberId 직접 지정
     * Postman / 로컬 테스트용
     */
    @GetMapping("/weekly/test")
    public WeeklyAssetSnapshotHistoryResponse getWeeklyHistoryForTest(
            @RequestParam(name = "memberId") Long memberId,
            @RequestParam(name = "weeks", defaultValue = "12") int weeks
    ) {
        log.info("[WEEKLY_ASSET_SNAPSHOT] test history query. memberId={}, weeks={}", memberId, weeks);
        return getAssetSnapshotHistoryUseCase.getWeeklyHistory(memberId, weeks);
    }
}
