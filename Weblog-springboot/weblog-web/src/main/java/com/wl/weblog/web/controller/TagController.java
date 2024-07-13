package com.wl.weblog.web.controller;

import com.wl.weblog.common.aspect.ApiOperationLog;
import com.wl.weblog.common.utils.Response;
import com.wl.weblog.web.model.vo.tag.FindTagArticlePageListReqVO;
import com.wl.weblog.web.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tag")
@Api(tags = "标签")
public class TagController {
    @Autowired
    private TagService tagService;

    /**
     * 前台获取标签列表
     * @return
     */
    @PostMapping("/list")
    @ApiOperation(value = "前台获取标签列表")
    @ApiOperationLog(description = "前台获取标签列表")
    public Response findTagList() {
        return tagService.findTagList();
    }

    /**
     * 前台获取标签下文章分页数据
     * @return
     */
    @PostMapping("/article/list")
    @ApiOperation(value = "前台获取标签下文章分页数据")
    @ApiOperationLog(description = "前台获取标签下文章分页数据")
    public Response findTagArticlePageList(@RequestBody @Validated FindTagArticlePageListReqVO findTagArticlePageListReqVO) {
        return tagService.findTagArticlePageList(findTagArticlePageListReqVO);
    }
}
