package com.sy.side.stock.scheduler;

import com.sy.side.stock.infrastructure.jpa.StockPriceDailyRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockPriceCleanupScheduler {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter BAS_DT_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final StockPriceDailyRepository stockPriceDailyRepository;

    /**
     * 주식 일자별 시세 데이터 정리
     * 매일 오전 11시 30분 실행
     * - 오늘 기준 5일 이전 bas_dt 데이터 삭제
     */
    @Transactional
    @Scheduled(cron = "0 30 11 * * *", zone = "Asia/Seoul")
    public void cleanupOldStockPrices() {
        String baseDate = LocalDate.now(KST)
                .minusDays(5)
                .format(BAS_DT_FORMATTER);

        int deletedCount = stockPriceDailyRepository.deleteByBasDtBefore(baseDate);
    }
}