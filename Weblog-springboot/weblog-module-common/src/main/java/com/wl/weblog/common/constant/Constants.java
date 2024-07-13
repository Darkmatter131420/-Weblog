package com.wl.weblog.common.constant;

import java.time.format.DateTimeFormatter;

public interface Constants {
    /**
     * 月-日格式
     */
    DateTimeFormatter MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
