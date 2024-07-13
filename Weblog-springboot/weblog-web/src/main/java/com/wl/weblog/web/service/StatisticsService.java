package com.wl.weblog.web.service;

import com.wl.weblog.common.utils.Response;

public interface StatisticsService {
    /**
     * 获取文章总数、分类总数、标签总数、总浏览量
     * @return
     */
    Response findInfo();
}
