package com.wl.weblog.admin.model.vo.blogsettings;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("博客基础信息修改 VO")
public class UpdateBlogSettingsReqVO {

    /**
     * 博客 LOGO
     */
    @NotBlank(message = "博客 LOGO 不能为空")
    private String logo;

    /**
     * 博客名称
     */
    @NotBlank(message = "博客名称不能为空")
    private String name;

    /**
     * 作者
     */
    @NotBlank(message = "博客作者不能为空")
    private String author;

    /**
     * 介绍语
     */
    @NotBlank(message = "博客介绍语不能为空")
    private String introduction;

    /**
     * 作者头像
     */
    @NotBlank(message = "博客头像不能为空")
    private String avatar;

    /**
     * github 主页地址
     */
    private String githubHomePage;

    /**
     * csdn 主页地址
     */
    private String csdnHomePage;

    /**
     * gitee 主页地址
     */
    private String giteeHomePage;

    /**
     * 知乎主页地址
     */
    private String zhihuHomePage;
}
