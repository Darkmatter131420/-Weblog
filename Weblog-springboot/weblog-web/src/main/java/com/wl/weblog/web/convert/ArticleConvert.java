package com.wl.weblog.web.convert;

import com.wl.weblog.common.domain.dos.ArticleDO;
import com.wl.weblog.web.model.vo.archive.FindArchiveArticleRspVO;
import com.wl.weblog.web.model.vo.article.FindIndexArticlePageListRspVO;
import com.wl.weblog.web.model.vo.category.FindCategoryArticlePageListRspVO;
import com.wl.weblog.web.model.vo.tag.FindTagArticlePageListRspVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ArticleConvert {
    /**
     * 初始化 convert 实例
     */
    ArticleConvert INSTANCE = Mappers.getMapper(ArticleConvert.class);

    /**
     * 将 DO 转换为 VO
     * @param bean
     * @return
     */
    @Mapping(target = "createDate", expression = "java(java.time.LocalDate.from(bean.getCreateTime()))")
    FindIndexArticlePageListRspVO convertDO2VO(ArticleDO bean);

    /**
     * 将 DO 转换为归档文章 VO
     * @param bean
     * @return
     */
    @Mapping(target = "createDate", expression = "java(java.time.LocalDate.from(bean.getCreateTime()))")
    @Mapping(target = "createMonth", expression = "java(java.time.YearMonth.from(bean.getCreateTime()))")
    FindArchiveArticleRspVO convertDO2ArchiveArticleVO(ArticleDO bean);



    /**
     * 将 DO 转换为分类文章 VO
     * @param bean
     * @return
     */
    @Mapping(target = "createDate", expression = "java(java.time.LocalDate.from(bean.getCreateTime()))")
    FindCategoryArticlePageListRspVO convertDO2CateforyArticleVO(ArticleDO bean);

    /**
     * 将 DO 转换为标签文章 VO
     * @param bean
     * @return
     */
    @Mapping(target = "createDate", expression = "java(java.time.LocalDate.from(bean.getCreateTime()))")
    FindTagArticlePageListRspVO convertDO2TagArticleVO(ArticleDO bean);
}