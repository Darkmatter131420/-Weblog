package com.wl.weblog.admin.service;

import com.wl.weblog.admin.model.vo.tag.AddTagReqVO;
import com.wl.weblog.admin.model.vo.tag.DeleteTagReqVO;
import com.wl.weblog.admin.model.vo.tag.FindTagPageListReqVO;
import com.wl.weblog.admin.model.vo.tag.SearchTagReqVO;
import com.wl.weblog.common.utils.PageResponse;
import com.wl.weblog.common.utils.Response;

public interface AdminTagService {

    /**
     * 添加标签
     * @param addTagReqVO
     * @return
     */
    Response addTag(AddTagReqVO addTagReqVO);

    /**
     * 标签分页数据获取
     * @param findTagPageListReqVO
     * @return
     */
    PageResponse findTagList(FindTagPageListReqVO findTagPageListReqVO);

    Response deleteTag(DeleteTagReqVO deleteTagReqVO);

    /**
     * 根据标签关键词模糊查询
     * @param searchTagReqVO
     * @return
     */
    Response searchTag(SearchTagReqVO searchTagReqVO);

    /**
     * 查询标签 Select 列表数据
     * @return
     */
    Response findTagSelectList();
}
