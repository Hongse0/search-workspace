package com.sy.side.stock.util;

import java.math.BigDecimal;
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

        // 주말이면 무조건 직전 금요일
        if (dow == DayOfWeek.SATURDAY) {
            return date.minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);
        }

        if (dow == DayOfWeek.SUNDAY) {
            return date.minusDays(2).format(DateTimeFormatter.BASIC_ISO_DATE);
        }

        // 평일이면 18:00 이전에는 전 영업일
        LocalTime cutoff = LocalTime.of(18, 0);

        if (now.toLocalTime().isBefore(cutoff)) {
            date = date.minusDays(1);

            if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
                date = date.minusDays(1);
            }

            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                date = date.minusDays(2);
            }
        }

        return date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    public static BigDecimal defaultZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    public static String resolveKrxPriceBaseDate(LocalDateTime now) {
        LocalDate date = now.toLocalDate();

        switch (date.getDayOfWeek()) {
            case MONDAY:
                date = date.minusDays(3); // 직전 금요일
                break;
            case SUNDAY:
                date = date.minusDays(2); // 금요일
                break;
            case SATURDAY:
                date = date.minusDays(1); // 금요일
                break;
            default:
                date = date.minusDays(1); // 전일
        }

        return date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }
}
