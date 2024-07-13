package com.wl.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wl.weblog.common.domain.dos.ArticleDO;
import com.wl.weblog.common.domain.dos.ArticleTagRelDO;
import com.wl.weblog.common.domain.dos.TagDO;
import com.wl.weblog.common.domain.mapper.ArticleMapper;
import com.wl.weblog.common.domain.mapper.ArticleTagRelMapper;
import com.wl.weblog.common.domain.mapper.TagMapper;
import com.wl.weblog.common.enums.ResponseCodeEnum;
import com.wl.weblog.common.exception.BizException;
import com.wl.weblog.common.utils.PageResponse;
import com.wl.weblog.common.utils.Response;
import com.wl.weblog.web.convert.ArticleConvert;
import com.wl.weblog.web.model.vo.tag.FindTagArticlePageListReqVO;
import com.wl.weblog.web.model.vo.tag.FindTagArticlePageListRspVO;
import com.wl.weblog.web.model.vo.tag.FindTagListRspVO;
import com.wl.weblog.web.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ArticleTagRelMapper articleTagMapper;

    @Autowired
    private ArticleMapper articleMapper;
    /**
     * 查询标签列表
     *
     * @return
     */
    @Override
    public Response findTagList() {
        List<TagDO> tagDOS = tagMapper.selectList(Wrappers.emptyWrapper());

        // DO 转 VO
        List<FindTagListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(tagDOS)) {
            vos = tagDOS.stream()
                    .map(tagDO -> FindTagListRspVO.builder()
                        .id(tagDO.getId())
                        .name(tagDO.getName())
                        .build())
            .collect(Collectors.toList());
        }
        return Response.success(vos);
    }

    /**
     * 查询标签下文章分页数据
     *
     * @param findTagArticlePageListReqVO
     * @return
     */
    @Override
    public Response findTagArticlePageList(FindTagArticlePageListReqVO findTagArticlePageListReqVO) {
        Long id = findTagArticlePageListReqVO.getId();
        Long current = findTagArticlePageListReqVO.getCurrent();
        Long size = findTagArticlePageListReqVO.getSize();

        TagDO tagDO = tagMapper.selectById(id);

        //判断标签是否存在
        if (Objects.isNull(tagDO)) {
            log.warn("标签不存在，id:{}", id);
            throw new BizException(ResponseCodeEnum.TAG_NOT_EXISTED);
        }

        // 查询文章标签关联表
        List<ArticleTagRelDO> articleTagRelDOS = articleTagMapper.selectListByTagId(id);

        // 若该标签下未发表任何文章
        if (CollectionUtils.isEmpty(articleTagRelDOS)) {
            log.warn("该标签下未发表任何文章，id:{}", id);
            return PageResponse.success(null,null);
        }

        // 获取文章 ID 列表
        List<Long> articleIds = articleTagRelDOS.stream()
                .map(ArticleTagRelDO::getArticleId)
                .collect(Collectors.toList());

        // 根据文章 ID 集合查询文章分页数据
        Page<ArticleDO> page = articleMapper.selectPageListByArticleIds(current, size, articleIds);
        List<ArticleDO> articleDOS = page.getRecords();

        // DO 转 VO
        List<FindTagArticlePageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(articleDOS)) {
            vos = articleDOS.stream().map(ArticleConvert.INSTANCE::convertDO2TagArticleVO)
                    .collect(Collectors.toList());
        }
        return PageResponse.success(page, vos);
    }
}
