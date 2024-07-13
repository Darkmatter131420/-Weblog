package com.wl.weblog.admin.model.vo.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindCategoryPageListRspVO {
    /**
     * 标签ID
     */
    private Long id;

    /**
     * 分标签名称
     */
    private String name;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
