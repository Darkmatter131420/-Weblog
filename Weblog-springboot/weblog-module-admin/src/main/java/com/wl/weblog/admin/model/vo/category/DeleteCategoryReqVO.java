package com.wl.weblog.admin.model.vo.category;

import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "删除分类 VO")
public class DeleteCategoryReqVO {

    @NotNull(message = "分类ID不能为空")
    private Long id;
}
