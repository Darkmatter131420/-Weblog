package com.wl.weblog.web.model.vo.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindStatisticsInfoRspVO {
    /**
     * 文章总数
     */
    private long articleTotalCount;

    /**
     * 分类总数
     */
    private  long categoryTotalCount;

    /**
     * 标签总数
     */
    private long tagTotalCount;

    /**
     * 总浏览量
     */
    private long pvTotalCount;
}
