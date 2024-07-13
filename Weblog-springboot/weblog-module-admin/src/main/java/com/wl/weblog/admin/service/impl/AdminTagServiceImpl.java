package com.wl.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wl.weblog.admin.model.vo.tag.*;
import com.wl.weblog.admin.service.AdminTagService;
import com.wl.weblog.common.domain.dos.ArticleTagRelDO;
import com.wl.weblog.common.domain.dos.TagDO;
import com.wl.weblog.common.domain.mapper.ArticleTagRelMapper;
import com.wl.weblog.common.domain.mapper.CategoryMapper;
import com.wl.weblog.common.domain.mapper.TagMapper;
import com.wl.weblog.common.enums.ResponseCodeEnum;
import com.wl.weblog.common.exception.BizException;
import com.wl.weblog.common.model.vo.SelectRspVO;
import com.wl.weblog.common.utils.PageResponse;
import com.wl.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminTagServiceImpl extends ServiceImpl<TagMapper, TagDO> implements AdminTagService {

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;

    /**
     * 添加标签
     *
     * @param addTagReqVO
     * @return
     */
    @Override
    public Response addTag(AddTagReqVO addTagReqVO) {
        List<TagDO> tagDOS = addTagReqVO.getTags()
                .stream().map(tagName -> TagDO.builder()
                        .name(tagName.trim())
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());
        try {
            saveBatch(tagDOS);
        }catch (Exception e){
            log.warn("该标签已存在", e);
        }
        return Response.success();
    }

    /**
     * 标签分页数据获取
     *
     * @param findTagPageListReqVO
     * @return
     */
    @Override
    public PageResponse findTagList(FindTagPageListReqVO findTagPageListReqVO) {

        Long current = findTagPageListReqVO.getCurrent();
        Long size = findTagPageListReqVO.getSize();
        String name = findTagPageListReqVO.getName();
        LocalDate startDate = findTagPageListReqVO.getStartDate();
        LocalDate endDate = findTagPageListReqVO.getEndDate();

        //分页查询
        Page<TagDO> page = tagMapper.selectPageList(current, size, name, startDate, endDate);

        List<TagDO> records = page.getRecords();

        // do 转 vo
        List<FindTagPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(records)) {
            vos = records.stream().map(tagDO -> FindTagPageListRspVO.builder()
                    .id(tagDO.getId())
                    .name(tagDO.getName())
                    .createTime(tagDO.getCreateTime())
                    .build())
                    .collect(Collectors.toList());
        }

        return PageResponse.success(page, vos);
    }

    @Override
    public Response deleteTag(DeleteTagReqVO deleteTagReqVO) {
        //获取标签id
        Long tagId = deleteTagReqVO.getId();

        //校验该标签是否已经被使用
        ArticleTagRelDO articleTagRelDO = articleTagRelMapper.selectOneByTagId(tagId);
        if (articleTagRelDO != null) {
            log.warn("==>该标签下包含文章，无法删除，tagId:{}", tagId);
            throw new BizException(ResponseCodeEnum.TAG_CAN_NOT_DELETE);
        }
        //删除标签
        int count = tagMapper.deleteById(tagId);

        return count == 1 ? Response.success() : Response.fail(ResponseCodeEnum.TAG_NOT_EXISTED);
    }

    /**
     * 根据标签关键词模糊查询
     *
     * @param searchTagReqVO
     * @return
     */
    @Override
    public Response searchTag(SearchTagReqVO searchTagReqVO) {
        String key = searchTagReqVO.getKey();

        // 执行模糊查询
        List<TagDO> tagDOS = tagMapper.selectByKey(key);

        // do 转 vo
        List<SelectRspVO> vos = null;
        if (!CollectionUtils.isEmpty(tagDOS)) {
            vos = tagDOS.stream()
                    .map(tagDO -> SelectRspVO.builder()
                            .label(tagDO.getName())
                            .value(tagDO.getId())
                            .build())
                    .collect(Collectors.toList());
        }
        return Response.success(vos);
    }

    /**
     * 查询标签 Select 列表数据
     *
     * @return
     */
    @Override
    public Response findTagSelectList() {
        // 查询所有标签, Wrappers.emptyWrapper() 表示查询条件为空
        List<TagDO> tagDOS = tagMapper.selectList(Wrappers.emptyWrapper());

        // do 转 vo
        List<SelectRspVO> vos = null;
        if (!CollectionUtils.isEmpty(tagDOS)) {
            vos = tagDOS.stream()
                    .map(tagDO -> SelectRspVO.builder()
                            .label(tagDO.getName())
                            .value(tagDO.getId())
                            .build())
                    .collect(Collectors.toList());
        }
        return Response.success(vos);
    }
}
