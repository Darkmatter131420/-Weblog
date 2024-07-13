package com.wl.weblog.web.model.vo.tag;

import com.wl.weblog.common.model.BasePageQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindTagArticlePageListReqVO extends BasePageQuery {
    /**
     * 标签id
     */
    @NotNull(message = "标签id不能为空")
    private Long id;
}
