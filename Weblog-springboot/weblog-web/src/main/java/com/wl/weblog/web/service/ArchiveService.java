package com.wl.weblog.web.service;

import com.wl.weblog.common.utils.Response;
import com.wl.weblog.web.model.vo.archive.FindArchiveArticlePageListReqVO;
import com.wl.weblog.web.model.vo.article.FindIndexArticlePageListReqVO;

public interface ArchiveService {
    /**
     * 获取文章归档分页数据
     * @param findArchivePageListReqVO
     * @return
     */
    Response findArchivePageList(FindArchiveArticlePageListReqVO findArchivePageListReqVO);
}
