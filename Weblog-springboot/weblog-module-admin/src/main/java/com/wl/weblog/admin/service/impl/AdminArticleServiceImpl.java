package com.wl.weblog.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.wl.weblog.admin.convert.ArticleDetailConvert;
import com.wl.weblog.admin.model.vo.article.*;
import com.wl.weblog.admin.service.AdminArticleService;
import com.wl.weblog.common.domain.dos.*;
import com.wl.weblog.common.domain.mapper.*;
import com.wl.weblog.common.enums.ResponseCodeEnum;
import com.wl.weblog.common.exception.BizException;
import com.wl.weblog.common.utils.PageResponse;
import com.wl.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminArticleServiceImpl implements AdminArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleContentMapper articleContentMapper;

    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;
    /**
     * 发布文章
     *
     * @param publishArticleReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response publishArticle(PublishArticleReqVO publishArticleReqVO) {
        // 1. VO 转 ArticleDO, 并保存
        ArticleDO articleDO = ArticleDO.builder()
                .title(publishArticleReqVO.getTitle())
                .cover(publishArticleReqVO.getCover())
                .summary(publishArticleReqVO.getSummary())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        articleMapper.insert(articleDO);

        // 拿到插入记录的主键 ID
        Long articleId = articleDO.getId();

        // 2. VO 转 ArticleContentDO, 并保存
        ArticleContentDO articleContentDO = ArticleContentDO.builder()
                .articleId(articleId)
                .content(publishArticleReqVO.getContent())
                .build();
        articleContentMapper.insert(articleContentDO);

        // 3. 处理文章关联的分类
        Long categoryId = publishArticleReqVO.getCategoryId();

        // 3.1 校验提交的分类是否真是存在
        CategoryDO categoryDO = categoryMapper.selectById(categoryId);
        if (Objects.isNull(categoryDO)){
            log.warn("==> 分类不存在, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }

        ArticleCategoryRelDO articleCategoryRelDO = ArticleCategoryRelDO.builder()
                .articleId(articleId)
                .categoryId(categoryId)
                .build();
        articleCategoryRelMapper.insert(articleCategoryRelDO);

        //4. 保存文章关联的标签集合
        List<String> publishTags = publishArticleReqVO.getTags();
        insertTags(articleId, publishTags);

        return Response.success();
    }

    /**
     * 保存标签
     * @param articleId
     * @param publishTags
     */
    private void insertTags(Long articleId, List<String> publishTags) {
        //筛选提交的标签（表中不存在的标签）
        List<String> notExistTags = null;
        //筛选已提交的标签（表中已存在的标签）
        List<String> existTags = null;

        // 1. 查询所有标签
        List<TagDO> tagDOS = tagMapper.selectList(null);

        // 如果表中还没有添加任何标签
        if (CollectionUtils.isEmpty(tagDOS)) {
            notExistTags = publishTags;
        }else {
            List<String> tagIds = tagDOS.stream().map(tagDO -> String.valueOf(tagDO.getId())).collect(Collectors.toList());
            //表中已添加标签，则需要筛选
            //通过标签id来筛选，包含对应 ID 则表示提交的标签是表中存在的
            existTags = publishTags.stream().filter(tagIds::contains).collect(Collectors.toList());

            //否则这是不存在的
            notExistTags = publishTags.stream().filter(publishTag -> !tagIds.contains(publishTag)).collect(Collectors.toList());

            //还有一种可能，按字符串名称交上来的标签，也有可能是表中已经存在的
            Map<String, Long> tagNameIdMap = tagDOS.stream().collect(Collectors.toMap(tagDO -> tagDO.getName().toLowerCase(), TagDO::getId));

            //使用迭代器进行安全的删除操作
            Iterator<String> iterator = notExistTags.iterator();
            while (iterator.hasNext()){
                String notExistTag = iterator.next();
                //转小写，若Map中包含相同的key，则表示该新标签是重复标签
                if (tagNameIdMap.containsKey(notExistTag.toLowerCase())){
                    //从不存在的标签集合中清除
                    iterator.remove();
                    //并将对应的ID添加到已存在的标签集合中
                    existTags.add(String.valueOf(tagNameIdMap.get(notExistTag.toLowerCase())));
                }
            }
        }

        // 2. 将提交上来的，已存在于表中的标签，文章-标签关系入库
        if (!CollectionUtils.isEmpty(existTags)){
            List<ArticleTagRelDO> articleTagRelDOS = Lists.newArrayList();
            existTags.forEach(tagId -> {
                ArticleTagRelDO articleTagRelDO = ArticleTagRelDO.builder()
                                .articleId(articleId)
                                .tagId(Long.valueOf(tagId))
                                .build();
                articleTagRelDOS.add(articleTagRelDO);
            });
            articleTagRelMapper.insertBatchSomeColumn(articleTagRelDOS);
        }

        // 3. 将提交上来的，表中不存在的标签，入库保存
        if (!CollectionUtils.isEmpty(notExistTags)){
            //需要先将标签入库，拿到对应标签ID后，再把文章标签关联关系入库
            List<ArticleTagRelDO> articleTagRelDOS = Lists.newArrayList();
            notExistTags.forEach(tagName -> {
                TagDO tagDO = TagDO.builder()
                        .name(tagName)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build();
                tagMapper.insert(tagDO);

                //拿到保存的标签ID
                Long tagId = tagDO.getId();

                //文章标签关系关联
                ArticleTagRelDO articleTagRelDO = ArticleTagRelDO.builder()
                        .articleId(articleId)
                        .tagId(tagDO.getId())
                        .build();
                articleTagRelDOS.add(articleTagRelDO);
            });
            articleTagRelMapper.insertBatchSomeColumn(articleTagRelDOS);
        }
    }

    /**
     * 删除文章
     *
     * @param deleteArticleReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteArticle(DeleteArticleReqVO deleteArticleReqVO) {
        Long articleId = deleteArticleReqVO.getId();
        // 1. 删除文章内容
        articleContentMapper.deleteByArticleId(articleId);

        // 2. 删除文章分类关联
        articleCategoryRelMapper.deleteByArticleId(articleId);

        // 3. 删除文章标签关联
        articleTagRelMapper.deleteByArticleId(articleId);

        // 4. 删除文章
        articleMapper.deleteById(articleId);

        return Response.success();
    }

    /**
     * 查询文章分页数据
     *
     * @param findArticlePageListReqVO
     * @return
     */
    @Override
    public Response findArticlePageList(FindArticlePageListReqVO findArticlePageListReqVO) {
       //获取当前页
        Long current = findArticlePageListReqVO.getCurrent();
        Long size = findArticlePageListReqVO.getSize();
        String title = findArticlePageListReqVO.getTitle();
        LocalDate startDate = findArticlePageListReqVO.getStartDate();
        LocalDate endDate = findArticlePageListReqVO.getEndDate();

        //执行分页查询
        Page<ArticleDO> articleDOPage = articleMapper.selectPageList(current, size, title, startDate, endDate);

        List<ArticleDO> articleDOS = articleDOPage.getRecords();

        //DO 转 VO
        List<FindArticlePageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(articleDOS)){
            vos = articleDOS.stream().map(articleDO -> FindArticlePageListRspVO.builder()
                        .id(articleDO.getId())
                        .title(articleDO.getTitle())
                        .cover(articleDO.getCover())
                        .createTime(articleDO.getCreateTime())
                        .build())
                .collect(Collectors.toList());
        }
        return PageResponse.success(articleDOPage, vos);
    }

    /**
     * 查询文章详情
     *
     * @param findArticleDetailReqVO
     * @return
     */
    @Override
    public Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO) {
        Long articleId = findArticleDetailReqVO.getId();
        // 1. 查询文章基本信息
        ArticleDO articleDO = articleMapper.selectById(articleId);

        if (Objects.isNull(articleDO)){
            log.warn("==> 查询的文章不存在, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 2. 查询文章内容
        ArticleContentDO articleContentDO = articleContentMapper.selectByArticleId(articleId);

        // 3. 查询文章分类
        ArticleCategoryRelDO articleCategoryRelDO = articleCategoryRelMapper.selectByArticleId(articleId);

        // 4. 查询文章标签
        List<ArticleTagRelDO> articleTagRelDOS = articleTagRelMapper.selectByArticleId(articleId);
        List<Long> tagIds = articleTagRelDOS.stream().map(ArticleTagRelDO::getTagId).collect(Collectors.toList());

        // DO 转 VO
        FindArticleDetailRspVO vo = ArticleDetailConvert.INSTANCE.convertDO2VO(articleDO);
        vo.setContent(articleContentDO.getContent());
        vo.setCategoryId(articleCategoryRelDO.getCategoryId());
        vo.setTagIds(tagIds);

        return Response.success(vo);
    }

    /**
     * 更新文章
     *
     * @param updateArticleReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response updateArticle(UpdateArticleReqVO updateArticleReqVO) {
        Long articleId = updateArticleReqVO.getId();
        // 1. 更新文章基本信息
        ArticleDO articleDO = ArticleDO.builder()
                .id(articleId)
                .title(updateArticleReqVO.getTitle())
                .cover(updateArticleReqVO.getCover())
                .summary(updateArticleReqVO.getSummary())
                .updateTime(LocalDateTime.now())
                .build();
        int count = articleMapper.updateById(articleDO);

        //根据更新结果判断文章是否存在
        if (count == 0){
            log.warn("==> 该文章不存在, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 2. 更新文章内容
        ArticleContentDO articleContentDO = ArticleContentDO.builder()
                .articleId(articleId)
                .content(updateArticleReqVO.getContent())
                .build();
        articleContentMapper.updateByArticleId(articleContentDO);

        // 3. 更新文章分类
        Long categoryId = updateArticleReqVO.getCategoryId();
        // 3.1 校验提交的分类是否真实存在
        CategoryDO categoryDO = categoryMapper.selectById(categoryId);
        if (Objects.isNull(categoryDO)){
            log.warn("==> 分类不存在, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }
        //先删除该文章关联的分类记录，再插入新的关联关系
        articleCategoryRelMapper.deleteByArticleId(articleId);
        ArticleCategoryRelDO articleCategoryRelDO = ArticleCategoryRelDO.builder()
                .articleId(articleId)
                .categoryId(categoryId)
                .build();
        articleCategoryRelMapper.insert(articleCategoryRelDO);

        // 4. 更新文章标签
        List<String> tags = updateArticleReqVO.getTags();
        // 4.1 删除文章标签关联
        articleTagRelMapper.deleteByArticleId(articleId);
        // 4.2 保存文章标签
        insertTags(articleId, tags);

        return Response.success();
    }
}
