package com.wl.weblog.admin.service;

import com.wl.weblog.admin.model.vo.category.AddCategoryReqVO;
import com.wl.weblog.admin.model.vo.category.DeleteCategoryReqVO;
import com.wl.weblog.admin.model.vo.category.FindCategoryPageListReqVO;
import com.wl.weblog.common.utils.PageResponse;
import com.wl.weblog.common.utils.Response;

public interface AdminCategoryService {

    /**
     * 添加分类
     * @param addCategoryReqVO
     * @return
     */
    Response addCategory(AddCategoryReqVO addCategoryReqVO);

    /**
     * 查询分类分页数据
     * @param findCategoryPageListReqVO
     * @return
     */
    PageResponse findCategoryPageList(FindCategoryPageListReqVO findCategoryPageListReqVO);

    /**
     * 删除分类
     * @param deleteCategoryReqVO
     * @return
     */
    Response deleteCategory(DeleteCategoryReqVO deleteCategoryReqVO);

    /**
     * 获取文章分类的Select列表数据
     * @return
     */
    Response findCategorySelectList();
}
