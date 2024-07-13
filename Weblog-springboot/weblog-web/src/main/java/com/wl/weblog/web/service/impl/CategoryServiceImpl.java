package com.wl.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wl.weblog.common.domain.dos.ArticleCategoryRelDO;
import com.wl.weblog.common.domain.dos.ArticleDO;
import com.wl.weblog.common.domain.dos.CategoryDO;
import com.wl.weblog.common.domain.mapper.ArticleCategoryRelMapper;
import com.wl.weblog.common.domain.mapper.ArticleMapper;
import com.wl.weblog.common.domain.mapper.CategoryMapper;
import com.wl.weblog.common.enums.ResponseCodeEnum;
import com.wl.weblog.common.exception.BizException;
import com.wl.weblog.common.utils.PageResponse;
import com.wl.weblog.common.utils.Response;
import com.wl.weblog.web.convert.ArticleConvert;
import com.wl.weblog.web.model.vo.category.FindCategoryArticlePageListReqVO;
import com.wl.weblog.web.model.vo.category.FindCategoryArticlePageListRspVO;
import com.wl.weblog.web.model.vo.category.FindCategoryListRspVO;
import com.wl.weblog.web.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;

    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 查询分类列表
     *
     * @return
     */
    @Override
    public Response findCategoryList() {
        // 查询所有分类
        List<CategoryDO> categoryDOS = categoryMapper.selectList(Wrappers.emptyWrapper());

        // DO 转 VO
        List<FindCategoryListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(categoryDOS)) {
            vos = categoryDOS.stream()
                    .map(categoryDO -> FindCategoryListRspVO.builder()
                        .id(categoryDO.getId())
                        .name(categoryDO.getName())
                        .build())
            .collect(Collectors.toList());
        }
        return Response.success(vos);
    }

    /**
     * 获取分类下文章分页数据
     *
     * @param findCategoryArticlePageListReqVO
     * @return
     */
    @Override
    public Response findCategoryArticlePageList(FindCategoryArticlePageListReqVO findCategoryArticlePageListReqVO) {
        Long current = findCategoryArticlePageListReqVO.getCurrent();
        Long size = findCategoryArticlePageListReqVO.getSize();
        Long categoryId = findCategoryArticlePageListReqVO.getId();

        CategoryDO categoryDO = categoryMapper.selectById(categoryId);

        //判断分类是否存在
        if (Objects.isNull(categoryDO)) {
            log.warn("==> 该分类不存在，categoryId:{}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }

        //查询文章分类关联表
        List<ArticleCategoryRelDO> articleCategoryRelDOS = articleCategoryRelMapper.selectListByCategoryId(categoryId);

        // 若该分类下未发布任何文章
        if (CollectionUtils.isEmpty(articleCategoryRelDOS)) {
            log.warn("==> 该分类下未发布任何文章，categoryId:{}", categoryId);
            return PageResponse.success(null, null);
        }

        List<Long> articleIds = articleCategoryRelDOS.stream()
                .map(ArticleCategoryRelDO::getArticleId)
                .collect(Collectors.toList());

        // 根据文章 ID 集合查询文章分页数据
        Page<ArticleDO> page = articleMapper.selectPageListByArticleIds(current, size, articleIds);
        List<ArticleDO> articleDOS = page.getRecords();

        // DO 转 VO
        List<FindCategoryArticlePageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(articleDOS)) {
            vos = articleDOS.stream()
                    .map(articleDO -> ArticleConvert.INSTANCE.convertDO2CateforyArticleVO(articleDO))
                    .collect(Collectors.toList());
        }

        return PageResponse.success(page, vos);
    }
}
