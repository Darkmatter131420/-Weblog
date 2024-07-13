package com.wl.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wl.weblog.common.domain.dos.ArticleDO;
import com.wl.weblog.common.domain.mapper.ArticleMapper;
import com.wl.weblog.common.domain.mapper.CategoryMapper;
import com.wl.weblog.common.domain.mapper.TagMapper;
import com.wl.weblog.common.utils.Response;
import com.wl.weblog.web.model.vo.statistics.FindStatisticsInfoRspVO;
import com.wl.weblog.web.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private TagMapper tagMapper;

    /**
     * 获取文章总数、分类总数、标签总数、总浏览量
     *
     * @return
     */
    @Override
    public Response findInfo() {
        // 获取文章总数
        Long articleTotalCount = articleMapper.selectCount(Wrappers.emptyWrapper());

        // 获取分类总数
        Long categoryTotalCount = categoryMapper.selectCount(Wrappers.emptyWrapper());

        // 获取标签总数
        Long tagTotalCount = tagMapper.selectCount(Wrappers.emptyWrapper());

        // 总浏览量
        List<ArticleDO> articleDOS = articleMapper.selectAllReadNum();
        Long pvTotalCount = 0L;

        if (!CollectionUtils.isEmpty(articleDOS)) {
            pvTotalCount = articleDOS.stream().mapToLong(ArticleDO::getReadNum).sum();
        }

        // 组装Vo类
        return Response.success(FindStatisticsInfoRspVO.builder()
                .articleTotalCount(articleTotalCount)
                .categoryTotalCount(categoryTotalCount)
                .tagTotalCount(tagTotalCount)
                .pvTotalCount(pvTotalCount)
                .build());
    }
}
