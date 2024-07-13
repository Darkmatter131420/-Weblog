package com.wl.weblog.common.model;

import lombok.Data;

@Data
public class BasePageQuery {
    /**
     * 当前页
     */
    private Long current = 1L;

    /**
     * 每页显示的记录数，默认每条显示10条
     */
    private Long size = 10L;
}
