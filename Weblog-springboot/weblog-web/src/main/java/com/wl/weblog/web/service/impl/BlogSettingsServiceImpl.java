package com.wl.weblog.web.service.impl;

import com.wl.weblog.common.domain.dos.BlogSettingsDO;
import com.wl.weblog.common.domain.mapper.BlogSettingsMapper;
import com.wl.weblog.common.utils.Response;
import com.wl.weblog.web.convert.BlogSettingsConvert;
import com.wl.weblog.web.model.vo.blogsettings.FindBlogSettingsDetailRspVO;
import com.wl.weblog.web.service.BlogSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BlogSettingsServiceImpl implements BlogSettingsService {
    @Autowired
    private BlogSettingsMapper blogSettingsMapper;
    /**
     * 查询博客设置信息
     *
     * @return
     */
    @Override
    public Response findDetail() {
        // 查询博客设置信息
        BlogSettingsDO blogSettingsDO = blogSettingsMapper.selectById(1L);

        // DO转VO
        FindBlogSettingsDetailRspVO vo = BlogSettingsConvert.INSTANCE.convertDO2VO(blogSettingsDO);

        return Response.success(vo);
    }
}
