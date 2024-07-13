package com.wl.weblog.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wl.weblog.admin.convert.BlogSettingsConvert;
import com.wl.weblog.admin.model.vo.blogsettings.FindBlogSettingsRspVO;
import com.wl.weblog.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import com.wl.weblog.admin.service.AdminBlogSettingsService;
import com.wl.weblog.common.domain.dos.BlogSettingsDO;
import com.wl.weblog.common.domain.mapper.BlogSettingsMapper;
import com.wl.weblog.common.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminBlogSettingsServiceImpl extends ServiceImpl<BlogSettingsMapper,BlogSettingsDO> implements AdminBlogSettingsService {

    @Autowired
    private BlogSettingsMapper blogSettingsMapper;

    /**
     * 更新博客设置信息
     *
     * @param updateBlogSettingsReqVO
     * @return
     */
    @Override
    public Response updateBlogSettings(UpdateBlogSettingsReqVO updateBlogSettingsReqVO) {
        // VO 转换为 DO
        BlogSettingsDO blogSettingsDO = BlogSettingsConvert.INSTANCE.convertVO2DO(updateBlogSettingsReqVO);
        blogSettingsDO.setId(1L);

        // 保存或更新 (当数据库中存在 id 为 1 的记录时，执行更新操作，否则执行插入操作)
        saveOrUpdate(blogSettingsDO);
        return Response.success();
    }

    /**
     * 获取博客设置详情
     *
     * @return
     */
    @Override
    public Response findDetail() {
        // 查询 id 为 1 的记录
        BlogSettingsDO blogSettingsDO = blogSettingsMapper.selectById(1L);
        // DO 转换为 VO
        FindBlogSettingsRspVO vo = BlogSettingsConvert.INSTANCE.convertDO2VO(blogSettingsDO);

        return Response.success(vo);
    }
}
