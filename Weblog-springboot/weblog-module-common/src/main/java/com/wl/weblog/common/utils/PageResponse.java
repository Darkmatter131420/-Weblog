package com.wl.weblog.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class PageResponse<T> extends Response<List<T>> {
    /**
     * 总记录数
     */
    private Long total = 0L;

    /**
     * 每页显示的记录数，默认每条显示10条
     */
    private Long size = 10L;

    /**
     * 当前页
     */
    private Long current;

    /**
     * 总页数
     */
    private long pages;

    /**
     * 成功响应
     * @param page Mybatis Plus 提供的分页接口
     * @param data
     * @return
     * @param <T>
     */
    public static <T> PageResponse<T> success(IPage page, List<T> data) {
        PageResponse<T> response = new PageResponse<>();
        response.setSuccess(true);
        response.setCurrent(Objects.isNull(page.getCurrent()) ? 1L : page.getCurrent());
        response.setSize(Objects.isNull(page.getSize()) ? 10L : page.getSize());
        response.setPages(Objects.isNull(page.getPages()) ? 0L : page.getPages());
        response.setTotal(Objects.isNull(page.getTotal()) ? 0L : page.getTotal());
        response.setData(data);
        return response;
    }
}
