package com.wl.weblog.common.aspect;

import com.wl.weblog.common.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class ApiOperationLogAspect {

    /** 以自定义 @ApiOperationLog 注解为切点，凡是添加 @ApiOperationLog 的方法都会执行环绕中的代码 */
    @Pointcut("@annotation(com.wl.weblog.common.aspect.ApiOperationLog)")
    public void apiOperationLog() {}

    /**
     * 环绕通知
     * @param joinPoint
     * @return
     * @throws Throwable
     */
     @Around("apiOperationLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
         try {
             // 请求开始的时间
             long startTime = System.currentTimeMillis();

             //MDC
             MDC.put("traceId", UUID.randomUUID().toString());

             //获取被请求的类名和方法名
             String className = joinPoint.getTarget().getClass().getSimpleName();
             String methodName = joinPoint.getSignature().getName();

             //请求入参
             Object[] args = joinPoint.getArgs();
             //入参转json字符串
             String argsJsonStr = Arrays.stream(args).map(toJsonStr()).collect(Collectors.joining(","));

             //功能描述信息
             String description = getApiOperationLogDescription(joinPoint);

             //打印请求相关参数
             log.info("====== 请求开始: [{}], 入参: {}, 请求类: {}, 请求方法: {} =================================== ",
                     description, argsJsonStr, className, methodName);

             //执行切点方法
             Object result = joinPoint.proceed();

             //执行耗时
             long executionTime = System.currentTimeMillis() - startTime;

             //打印出参等相关信息
             log.info("====== 请求结束: [{}], 出参: {}, 执行耗时: {} 毫秒 =============================== ",
                     description, executionTime, JsonUtil.toJSonString(result));
             return result;
         }finally {
            //清除MDC
            MDC.clear();
         }
    }

        /**
         * 获取 @ApiOperationLog 注解中的 description 属性
         * @param joinPoint
         * @return
         */
    private String getApiOperationLogDescription(ProceedingJoinPoint joinPoint) {
        //从 ProceedingJoinPoint 中获取MethodSignature
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        //使用signature获取当前被注解的 Method
        Method method = signature.getMethod();

        //从 Method 中提取 LogExecution 注解
        ApiOperationLog apiOperationLog = method.getAnnotation(ApiOperationLog.class);

        //从 LogExecution 注解中获取 description 属性
        return apiOperationLog.description();
    }

    private Function<Object,String> toJsonStr() {
        return arg -> JsonUtil.toJSonString(arg);
    }
}
