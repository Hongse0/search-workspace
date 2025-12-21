package com.sy.side.stock.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class StockUtil {
    /**
     * KRX 기준일자(basDt) 자동 계산 (간이 버전)
     * - 주말: 직전 금요일
     * - 평일: 18:00 이후면 당일, 그 전이면 전일
     */
    public static String resolveKrxBaseDate(LocalDateTime now) {
        LocalDate date = now.toLocalDate();
        DayOfWeek dow = date.getDayOfWeek();

        // 주말이면 금요일로 이동
        if (dow == DayOfWeek.SATURDAY) date = date.minusDays(1);
        if (dow == DayOfWeek.SUNDAY)   date = date.minusDays(2);

        // 평일이면 시간 기준 처리 (장 마감 이후를 안전하게 18:00로 둠)
        LocalTime cutoff = LocalTime.of(18, 0);
        if (now.toLocalTime().isBefore(cutoff)) {
            // 장 마감 전에는 전 영업일로 잡는게 안전
            date = date.minusDays(1);

            // 전날이 주말이면 금요일로 보정
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY) date = date.minusDays(1);
            if (date.getDayOfWeek() == DayOfWeek.SUNDAY)   date = date.minusDays(2);
        }

        return date.format(DateTimeFormatter.BASIC_ISO_DATE); // YYYYMMDD
    }
}
