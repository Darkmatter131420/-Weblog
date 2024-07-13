package com.wl.weblog.web.service;

import com.wl.weblog.common.utils.Response;
import com.wl.weblog.web.model.vo.tag.FindTagArticlePageListReqVO;

public interface TagService {
    /**
     * 查询标签列表
     * @return
     */
    Response findTagList();

    /**
     * 查询标签下文章分页数据
     * @param findTagArticlePageListReqVO
     * @return
     */
    Response findTagArticlePageList(FindTagArticlePageListReqVO findTagArticlePageListReqVO);
}
