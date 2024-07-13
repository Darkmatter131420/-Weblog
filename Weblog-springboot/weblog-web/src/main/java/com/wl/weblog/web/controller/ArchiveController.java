package com.wl.weblog.web.controller;

import com.wl.weblog.common.aspect.ApiOperationLog;
import com.wl.weblog.common.utils.Response;
import com.wl.weblog.web.model.vo.archive.FindArchiveArticlePageListReqVO;
import com.wl.weblog.web.service.ArchiveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/archive")
@Api(tags = "归档模块")
public class ArchiveController {
    @Autowired
    private ArchiveService archiveService;

    @PostMapping("/list")
    @ApiOperation(value = "获取文章归档分页数据")
    @ApiOperationLog(description = "获取文章归档分页数据")
    public Response findArchivePageList(@RequestBody FindArchiveArticlePageListReqVO findArchivePageListReqVO) {
        return archiveService.findArchivePageList(findArchivePageListReqVO);
    }
}
