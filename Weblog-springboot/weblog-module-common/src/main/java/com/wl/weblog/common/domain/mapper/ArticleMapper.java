package com.wl.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wl.weblog.common.domain.dos.ArticleDO;
import com.wl.weblog.common.domain.dos.ArticlePublishCountDO;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public interface ArticleMapper extends BaseMapper<ArticleDO> {

    /**
     * 分页查询
     * @param current 当前页码
     * @param size 每页展示的数据量
     * @param title 文章标题
     * @param startDate 发布的起始日期
     * @param endDate 发布的结束日期
     * @return
     */
    default Page<ArticleDO> selectPageList(Long current, Long size, String title, LocalDate startDate, LocalDate endDate) {
        //分页对象
        Page<ArticleDO> page = new Page<>(current, size);

        //构建查询条件
        LambdaQueryWrapper<ArticleDO> Wrapper = Wrappers.<ArticleDO>lambdaQuery()
                .like(StringUtils.isNotBlank(title), ArticleDO::getTitle, title)
                .ge(Objects.nonNull(startDate), ArticleDO::getCreateTime, startDate)
                .le(Objects.nonNull(endDate), ArticleDO::getCreateTime, endDate)
                .orderByDesc(ArticleDO::getCreateTime);//按创建时间倒序排序

        return selectPage(page, Wrapper);
    }

    /**
     * 根据文章 ID 批量分页查询
     * @param current 当前页码
     * @param size 每页展示的数据量
     * @param articleIds
     * @return
     */
    default Page<ArticleDO> selectPageListByArticleIds(Long current, Long size, List<Long> articleIds) {
        //分页对象
        Page<ArticleDO> page = new Page<>(current, size);

        //构建查询条件
        LambdaQueryWrapper<ArticleDO> Wrapper = Wrappers.<ArticleDO>lambdaQuery()
                .in(ArticleDO::getId, articleIds)
                .orderByDesc(ArticleDO::getCreateTime);//按创建时间倒序排序

        return selectPage(page, Wrapper);
    }

    /**
     * 查询上一篇文章
     * @param articleId
     * @return
     */
    default ArticleDO selectPreArticle(Long articleId) {
        return selectOne(Wrappers.<ArticleDO>lambdaQuery()
                .orderByAsc(ArticleDO::getId)
                .gt(ArticleDO::getId, articleId)
                .last("limit 1"));
    }

    /**
     * 查询下一篇文章
     * @param articleId
     * @return
     */
    default ArticleDO selectNextArticle(Long articleId) {
        return selectOne(Wrappers.<ArticleDO>lambdaQuery()
                .orderByDesc(ArticleDO::getId)
                .lt(ArticleDO::getId, articleId)
                .last("limit 1"));
    }

    /**
     * 阅读量+1
     * @param articleId\
     * @return
     */
    default int increaseReadNum (Long articleId) {
        return update(null, Wrappers.<ArticleDO>lambdaUpdate()
                .setSql("read_num = read_num + 1")
                .eq(ArticleDO::getId, articleId));
    }

    /**
     * 查询所有记录的阅读量
     * @return
     */
    default List<ArticleDO> selectAllReadNum() {
        // 设置仅查询阅读量字段
        return selectList(Wrappers.<ArticleDO>lambdaQuery()
                .select(ArticleDO::getReadNum));
    }

    /**
     * 按日分组，并统计每日发布的文章数量
     * @param startDate
     * @param endDate
     * @return
     */
    @Select("SELECT DATE(create_time ) AS date, COUNT(*) AS count\n" +
            "FROM t_article\n" +
            "WHERE create_time >= #{startDate} AND create_time <= #{endDate}\n" +
            "GROUP BY DATE(create_time)")
    List<ArticlePublishCountDO> selectDateArticlePublishCount(LocalDate startDate, LocalDate endDate);
}
