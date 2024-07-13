package com.wl.weblog.common.aspect;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ApiOperationLog {
    /**
     *
     * API功能描述
     *
     * @return
     */
    String description() default "";
}
