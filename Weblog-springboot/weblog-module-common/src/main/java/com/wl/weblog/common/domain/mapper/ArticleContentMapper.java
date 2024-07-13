package com.wl.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wl.weblog.common.domain.dos.ArticleContentDO;

public interface ArticleContentMapper extends BaseMapper<ArticleContentDO> {

    /**
     * 根据文章 ID 删除记录
     * @param articleId
     * @return
     */
    default int deleteByArticleId(Long articleId) {
        return delete(Wrappers.<ArticleContentDO>lambdaQuery().eq(ArticleContentDO::getArticleId, articleId));
    }

    /**
     * 根据文章 ID 查询文章内容
     * @param articleId
     * @return
     */
    default ArticleContentDO selectByArticleId(Long articleId) {
        return selectOne(Wrappers.<ArticleContentDO>lambdaQuery().eq(ArticleContentDO::getArticleId, articleId));
    }

    /**
     * 根据文章 ID 更新文章内容
     * @param articleContentDO
     */
    default int updateByArticleId(ArticleContentDO articleContentDO) {
        return update(articleContentDO,
                Wrappers.<ArticleContentDO>lambdaUpdate()
                        .eq(ArticleContentDO::getArticleId, articleContentDO.getArticleId()));
    }
}
