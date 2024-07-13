package com.wl.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wl.weblog.common.domain.dos.StatisticsArticlePVDO;

import java.time.LocalDate;
import java.util.List;


public interface StatisticsArticlePVMapper extends BaseMapper<StatisticsArticlePVDO> {

    /**
     * 对指定日期的 文章 PV 访问量 +1
     * @param date
     * @return
     */
    default int increasePVCount(LocalDate date) {
        return update(null, Wrappers.<StatisticsArticlePVDO>lambdaUpdate()
                .setSql("pv_count = pv_count + 1")
                .eq(StatisticsArticlePVDO::getPvDate, date));
    }

    /**
     * 查询最近一周的文章 PV 访问量记录
     * @return
     */
    default List<StatisticsArticlePVDO> selectLatestWeekRecords() {
        return selectList(Wrappers.<StatisticsArticlePVDO>lambdaQuery()
                .le(StatisticsArticlePVDO::getPvDate, LocalDate.now().plusDays(1))
                .orderByDesc(StatisticsArticlePVDO::getPvDate)
                .last("limit 7"));
    }
}
