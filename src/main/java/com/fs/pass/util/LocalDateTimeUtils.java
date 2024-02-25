package com.fs.pass.util;

import io.micrometer.common.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class LocalDateTimeUtils {
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static String format(final LocalDateTime localDateTime) {
        return localDateTime.format(YYYY_MM_DD_HH_MM);

    }

    public static String format(final LocalDateTime localDateTime, DateTimeFormatter formatter) {
        return localDateTime.format(formatter);

    }

    public static LocalDateTime parse(final String localDateTimeString) {
        if (StringUtils.isBlank(localDateTimeString)) { // StringUtils.isBlank: Hibernate에서 제공하는 유틸리티 - null 또는 공백 문자열 체크
            return null;
        }
        return LocalDateTime.parse(localDateTimeString, YYYY_MM_DD_HH_MM);

    }

    public static int getWeekOfYear(final LocalDateTime localDateTime) {
        return localDateTime.get(WeekFields.of(Locale.KOREA).weekOfYear());

    }

}
