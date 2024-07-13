package com.wl.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wl.weblog.admin.model.vo.category.AddCategoryReqVO;
import com.wl.weblog.admin.model.vo.category.DeleteCategoryReqVO;
import com.wl.weblog.admin.model.vo.category.FindCategoryPageListReqVO;
import com.wl.weblog.admin.model.vo.category.FindCategoryPageListRspVO;
import com.wl.weblog.admin.service.AdminCategoryService;
import com.wl.weblog.common.domain.dos.ArticleCategoryRelDO;
import com.wl.weblog.common.domain.dos.CategoryDO;
import com.wl.weblog.common.domain.mapper.ArticleCategoryRelMapper;
import com.wl.weblog.common.domain.mapper.CategoryMapper;
import com.wl.weblog.common.enums.ResponseCodeEnum;
import com.wl.weblog.common.exception.BizException;
import com.wl.weblog.common.model.vo.SelectRspVO;
import com.wl.weblog.common.utils.PageResponse;
import com.wl.weblog.common.utils.Response;
//import jdk.internal.org.jline.utils.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminCategoryServiceImpl implements AdminCategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    /**
     * 添加分类
     *
     * @param addCategoryReqVO@return
     */
    @Override
    public Response addCategory(AddCategoryReqVO addCategoryReqVO) {
        String categoryName = addCategoryReqVO.getName();

        // 先判断该分类是否已经存在
        CategoryDO categoryDO = categoryMapper.selectByName(categoryName);

        if (Objects.nonNull(categoryDO)) {
            log.warn("分类名称： {}, 此分类已存在", categoryName);
            throw new BizException(ResponseCodeEnum.CATEGORY_NAME_IS_EXISTED);
        }

        //构建DO类
        CategoryDO insertCategoryDO = CategoryDO.builder().name(addCategoryReqVO.getName().trim()).build();

        // 执行 insert
        categoryMapper.insert(insertCategoryDO);

        return Response.success();
    }

    /**
     * 查询分类分页数据
     *
     * @param findCategoryPageListReqVO
     * @return
     */
    @Override
    public PageResponse findCategoryPageList(FindCategoryPageListReqVO findCategoryPageListReqVO) {
        //获取当前页、以及每一页需要展示的数据数量
        Long current = findCategoryPageListReqVO.getCurrent();
        Long size = findCategoryPageListReqVO.getSize();
        String name = findCategoryPageListReqVO.getName();
        LocalDate startDate = findCategoryPageListReqVO.getStartDate();
        LocalDate endDate = findCategoryPageListReqVO.getEndDate();


        //执行分页查询
        Page<CategoryDO> categoryDOPage = categoryMapper.selectPageList(current, size, name, startDate, endDate);

        List<CategoryDO> categoryDOS = categoryDOPage.getRecords();

        // DO 转 VO
        List<FindCategoryPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(categoryDOS)) {
            vos = categoryDOS.stream()
                    .map(categoryDO -> FindCategoryPageListRspVO.builder()
                            .id(categoryDO.getId())
                            .name(categoryDO.getName())
                            .createTime(categoryDO.getCreateTime())
                            .build())
                    .collect(Collectors.toList());
        }

        return PageResponse.success(categoryDOPage, vos);
    }

    /**
     * 删除分类
     *
     * @param deleteCategoryReqVO
     * @return
     */
    @Override
    public Response deleteCategory(DeleteCategoryReqVO deleteCategoryReqVO) {
        //分类ID
        Long categoryId = deleteCategoryReqVO.getId();

        //校验该分类下是否已经有文章，若有，则提示需要先删除分类下面所有的文章，才能删除分类
        ArticleCategoryRelDO articleCategoryRelDO = articleCategoryRelMapper.selectOneByCategoryId(categoryId);

        if (Objects.nonNull(articleCategoryRelDO)) {
            log.warn("==>此分类下已有文章，无法删除，categoryId:{}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_CAN_NOT_DELETE);
        }
        //删除分类
        categoryMapper.deleteById(categoryId);

        return Response.success();
    }

    /**
     * 获取文章分类的Select列表数据
     *
     * @return
     */
    @Override
    public Response findCategorySelectList() {
        //查询所有的分类
        List<CategoryDO> categoryDOS = categoryMapper.selectList(null);

        // DO 转 VO
        List<SelectRspVO> selectRspVOS = null;

        if (!CollectionUtils.isEmpty(categoryDOS)) {
            selectRspVOS = categoryDOS.stream()
                    .map(categoryDO -> SelectRspVO.builder()
                            .label(categoryDO.getName())
                            .value(categoryDO.getId())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(selectRspVOS);
    }
}
